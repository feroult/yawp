package io.yawp.servlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
            String className = extractClassName(json);
            endpoint = compile(className, json);
        } catch (Exception ex) {
            throw new HttpException(422, "Compilation failure: " + ex);
        }
        r.getFeatures().addEndpoint(endpoint);
        throw new HttpException(200, "Recieved: " + Arrays.toString(endpoint.getDeclaredFields()));
    }

    public static String extractClassName(String clazz) {
        // TODO very very pog, find better way to do this!

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
        List<String> opts = Arrays.asList("-cp", yawpPath + ":.");
        SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
        CompiledCode compiledCode = new CompiledCode(className);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceCode);
        DynamicClassLoader cl = new DynamicClassLoader(MetaHandler.class.getClassLoader());
        ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), compiledCode, cl) {};
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, null, opts, null, compilationUnits);
        task.call();
        return cl.loadClass(className);
    }

}