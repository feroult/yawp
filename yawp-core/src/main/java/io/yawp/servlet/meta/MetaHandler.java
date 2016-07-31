package io.yawp.servlet.meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.mdkt.compiler.CompiledCode;
import org.mdkt.compiler.DynamicClassLoader;
import org.mdkt.compiler.ExtendedStandardJavaFileManager;
import org.mdkt.compiler.SourceCode;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.http.RequestContext;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.Repository;

public class MetaHandler {

	private static final Logger logger = Logger.getLogger(MetaHandler.class.getCanonicalName());

	private MetaHandler() {
	}

	public static void handle(Repository r, RequestContext ctx) throws HttpException {
		String uri = ctx.getUri().replaceAll("^\\/?_meta\\/?", "");
		Set<Class<?>> css = r.getFeatures().getEndpointClazzes();
		Class<?> cs = r.getClazzByKind(uri);
		switch (ctx.getHttpVerb()) {
		case DELETE:
			if (uri.isEmpty()) {
				for (Class<?> ep : css) {
					r.getFeatures().delete(r.getEndpointFeatures(ep));
				}
			} else {
				if (cs == null) {
					throw new HttpException(404);
				}
				r.getFeatures().delete(r.getEndpointFeatures(cs));
			}
			throw new HttpException(200, "Deleted");
		case GET:
			if (uri.isEmpty()) {
				List<MetaEndpoint> eps = new ArrayList<>(css.size());
				for (Class<?> ep : css) {
					if (!infra(ep)) {
						eps.add(new MetaEndpoint(r.getEndpointFeatures(ep)));
					}
				}
				respond(ctx, eps);
			} else {
				Class<?> clazz = cs;
				if (clazz == null || infra(clazz)) {
					throw new HttpException(404);
				}
				MetaEndpoint ep = new MetaEndpoint(r.getEndpointFeatures(clazz));
				respond(ctx, ep);
			}
			break;
		case POST:
			createNew(r, ctx);
			break;
		case OPTIONS:
			// Just for cross domain
			throw new HttpException(200);
		case PATCH:
		case PUT:
			throw new HttpException(501, "Can't PATCH/PUT for now. Delete and create again.");
		}
	}

	private static boolean infra(Class<?> ep) {
		return KindResolver.getKindFromClass(ep).startsWith("__yawp");
	}

	private static void respond(RequestContext ctx, Object obj) {
		try {
			ctx.resp().getWriter().write(JsonUtils.to(obj));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void createNew(Repository r, RequestContext ctx) {
		String json = ctx.getJson();
		Class<?> endpoint;
		try {
			String className = extractClassName(json);
			logger.info("className: " + className);
			endpoint = compile(className, json);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Compilation failure!", ex);
			throw new HttpException(422, "Compilation failure: " + ex);
		}
		r.getFeatures().addEndpoint(endpoint);
		throw new HttpException(200, "Recieved: " + Arrays.toString(endpoint.getDeclaredFields()));
	}

	// TODO very very pog, find better way to do this!
	public static String extractClassName(String clazz) {
		String pkg = "\\s*package\\s*([a-zA-Z\\.]*)\\s*;\\s*";
		String importList = "(?:import\\s*[a-zA-Z\\.]*\\s*;\\s*)*";
		String endpointAnn = "\\@Endpoint\\s*\\(path\\s*\\=\\s*\\\"[a-zA-Z\\/]*\\\"\\)\\s*";
		String classDef = "(?:public|private|protected)?\\s*class\\s*([a-zA-Z]*)\\s*\\{.*";

		Pattern r = Pattern.compile(pkg + importList + endpointAnn + classDef);

		Matcher m = r.matcher(clazz);
		if (m.find()) {
			return m.group(1) + "." + m.group(2);
		}
		throw new HttpException(422, "Couldn't extract class name from : " + clazz);
	}

	private static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

	public static Class<?> compile(String className, String sourceCodeInText) throws Exception {
		String yawpPath = MetaHandler.class.getProtectionDomain().getCodeSource().getLocation().toString();
		logger.info("yawpPath: " + yawpPath);
		List<String> opts = Arrays.asList("-cp", yawpPath + ":.");
		SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
		CompiledCode compiledCode = new CompiledCode(className);
		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceCode);
		DynamicClassLoader cl = new DynamicClassLoader(MetaHandler.class.getClassLoader());
		ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), compiledCode, cl) {
		};
		JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, null, opts, null, compilationUnits);
		task.call();
		return cl.loadClass(className);
	}

}