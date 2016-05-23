package io.yawp.plugin.mojos.devserver;

import io.yawp.plugin.devserver.DevServer;
import io.yawp.plugin.devserver.appengine.SdkResolver;
import io.yawp.plugin.devserver.base.MojoWrapper;
import io.yawp.plugin.mojos.base.ClassLoaderBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "devserver", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class DevServerMojo extends DevserverAbstractMojo {

    @Parameter(property = "yawp.address", defaultValue = "0.0.0.0")
    private String address;

    @Parameter(property = "yawp.port", defaultValue = "8080")
    private String port;

    @Parameter(property = "yawp.fullScanSeconds", defaultValue = "3")
    private String fullScanSeconds;

    @Parameter(property = "yawp.hotDeployDir", defaultValue = "${basedir}/target/classes")
    protected String hotDeployDir;


    @Override
    public void run() throws MojoExecutionException {
        createDevServer().run();
    }

//    private DevServer createDevServer() {
//        ClassLoader classLoader = createDevServerClassLoader();
//
//        try {
//            Class<?> clazz = classLoader.loadClass(DevServer.class.getName());
//            Constructor<?> constructor = clazz.getConstructor(MojoWrapper.class);
//            return (DevServer) constructor.newInstance(createMojoWrapper(classLoader));
//
//        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private DevServer createDevServer() {
        ClassLoader classLoader = createDevServerClassLoader();
        return new DevServer(createMojoWrapper(classLoader));
    }

    private MojoWrapper createMojoWrapper(ClassLoader classLoader) {
        MojoWrapper mojo = new MojoWrapper();
        mojo.setMojo(this);
        mojo.setClassLoader(classLoader);
        mojo.setRepoSystem(getRepoSystem());
        mojo.setRepoSession(getRepoSession());
        mojo.setProjectRepos(getProjectRepos());
        mojo.setPluginRepos(getPluginRepos());
        mojo.setProject(getProject());
        mojo.setEnv(getEnv());
        mojo.setBaseDir(getBaseDir());
        mojo.setAppDir(getAppDir());
        mojo.setAddress(getAddress());
        mojo.setPort(getPort());
        mojo.setFullScanSeconds(getFullScanSeconds());
        mojo.setHotDeployDir(getHotDeployDir());
        mojo.setShutdownPort(getShutdownPort());
        mojo.setAppengine(isAppengine());
        return mojo;
    }

    public String getHotDeployDir() {
        return hotDeployDir;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return Integer.valueOf(port);
    }

    public int getFullScanSeconds() {
        return Integer.valueOf(fullScanSeconds);
    }

    protected URLClassLoader createDevServerClassLoader() {
        ClassLoaderBuilder builder = new ClassLoaderBuilder();
        builder.addRuntime(this);
        if (isAppengine()) {
            builder.add(getAppengineCustomClasspathElements());
        }
        return builder.build();
    }


    public List<String> getAppengineCustomClasspathElements() {
        String sdkRoot = resolveSdkRoot();

        List<String> elements = new ArrayList<String>();
        elements.add(sdkRoot + "/lib/shared/servlet-api.jar");
        elements.add(sdkRoot + "/lib/shared/el-api.jar");
        elements.add(sdkRoot + "/lib/shared/jsp-api.jar");
        elements.add(sdkRoot + "/lib/impl/appengine-local-runtime.jar");
        elements.add(sdkRoot + "/lib/shared/appengine-local-runtime-shared.jar");
        elements.add(sdkRoot + "/lib/java-managed-vm/appengine-java-vmruntime/lib/ext/appengine-vm-runtime.jar");
        return elements;
    }

    private String resolveSdkRoot() {
        try {
            @SuppressWarnings("unchecked")
            File sdkBaseDir = SdkResolver.getSdk(getProject(), getRepoSystem(), getRepoSession(), getPluginRepos(),
                    getProjectRepos());

            System.setProperty("appengine.sdk.root", sdkBaseDir.getCanonicalPath());

            return sdkBaseDir.getAbsolutePath();
        } catch (MojoExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}