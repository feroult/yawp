package io.yawp.servlet;

import java.util.Arrays;
import java.util.List;

import io.yawp.repository.*;
import io.yawp.commons.http.HttpException;

import org.mdkt.compiler.*;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class MetaHandler {

	private MetaHandler()  {}

	public static void handle(Repository r, String json) throws HttpException {
		Class<?> endpoint;
		try {
			endpoint = compile("test.models.Type", json);
		} catch (Exception ex) {
			throw new HttpException(422, "Compilation failure: " + ex);
		}
		r.getFeatures().addEndpoint(endpoint);
		throw new HttpException(200, "Recieved: " + Arrays.toString(endpoint.getDeclaredFields()));
	}

    private static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

    public static Class<?> compile(String className, String sourceCodeInText) throws Exception {
    	String yawpPath = MetaHandler.class.getProtectionDomain().getCodeSource().getLocation().toString();
    	System.out.println("cp:" + yawpPath + ":.");
    	List<String> opts = Arrays.asList("-cp", yawpPath + ":.");
        SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
        CompiledCode compiledCode = new CompiledCode(className);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceCode);
        DynamicClassLoader cl = new DynamicClassLoader(MetaHandler.class.getClassLoader());
        ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), compiledCode, cl) {};
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, null, opts, null, compilationUnits);
        boolean result = task.call();
        return cl.loadClass(className);
    }

}