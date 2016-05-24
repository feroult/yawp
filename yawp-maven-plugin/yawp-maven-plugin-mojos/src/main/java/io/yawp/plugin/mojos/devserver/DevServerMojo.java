package io.yawp.plugin.mojos.devserver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.plugin.devserver.MojoWrapper;
import io.yawp.plugin.mojos.base.ClassLoaderBuilder;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = "devserver", requiresDependencyResolution = ResolutionScope.TEST)
public class DevServerMojo extends DevserverAbstractMojo {

    private static final String YAWP_GROUP_ID = "io.yawp";

    private static final String YAWP_ARTIFACT_ID = "yawp";

    private static final String YAWP_DEVSERVER_ARTIFACT_ID = "yawp-maven-plugin-devserver";

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
        getLog().info("Starting webserver at: " + getAppDir());
        runDevServer();
    }

    private void runDevServer() {
        ClassLoader classLoader = createDevServerClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            Class<?> clazz = classLoader.loadClass("io.yawp.plugin.devserver.DevServer");
            Constructor<?> constructor = clazz.getConstructor(String.class);
            Object devserver = constructor.newInstance(createMojoWrapperJson(classLoader));
            Method run = clazz.getMethod("run");
            run.invoke(devserver);

        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private String createMojoWrapperJson(ClassLoader classLoader) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        MojoWrapper mojo = new MojoWrapper();
        mojo.setEnv(getEnv());
        mojo.setBaseDir(getBaseDir());
        mojo.setAppDir(getAppDir());
        mojo.setAddress(getAddress());
        mojo.setPort(getPort());
        mojo.setFullScanSeconds(getFullScanSeconds());
        mojo.setHotDeployDir(getHotDeployDir());
        mojo.setShutdownPort(getShutdownPort());
        mojo.setAppengine(isAppengine());
        return JsonUtils.to(mojo);
    }

    private void set(Object mojo, String setter, Object value) {
        try {
            if (value == null) {
                return;
            }
            Method method = mojo.getClass().getMethod(setter, value.getClass());
            method.invoke(mojo, value);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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
        builder.add(resolveDevServerJar());
        if (isAppengine()) {
            builder.add(getAppengineCustomClasspathElements());
        }
        return builder.build();
    }


    public List<String> getAppengineCustomClasspathElements() {
        String sdkRoot = resolveSdkRoot();

        List<String> elements = new ArrayList<String>();

        //elements.add(sdkRoot + "/lib/shared/servlet-api.jar");
        elements.add(sdkRoot + "/lib/shared/el-api.jar");
        elements.add(sdkRoot + "/lib/shared/jsp-api.jar");
        elements.add(sdkRoot + "/lib/impl/appengine-local-runtime.jar");
        elements.add(sdkRoot + "/lib/shared/appengine-local-runtime-shared.jar");
        elements.add(sdkRoot + "/lib/java-managed-vm/appengine-java-vmruntime/lib/ext/appengine-vm-runtime.jar");
        return elements;
    }

    protected boolean isAppengine() {
        if (project.getGroupId().equals(YAWP_GROUP_ID) && project.getArtifactId().equals(YAWP_ARTIFACT_ID)) {
            return true;
        }

        for (Dependency dependency : project.getDependencies()) {
            if (dependency.getGroupId().equals(YAWP_GROUP_ID) && dependency.getArtifactId().equals(YAWP_ARTIFACT_ID)) {
                return true;
            }
        }
        return false;
    }


    public String resolveDevServerJar() {
        String version = getYawpVersion();
        List<RemoteRepository> allRepos = ImmutableList.copyOf(Iterables.concat(getProjectRepos()));

        ArtifactRequest request = new ArtifactRequest(new DefaultArtifact(YAWP_GROUP_ID, YAWP_DEVSERVER_ARTIFACT_ID, "jar", version), allRepos,
                null);

        ArtifactResult result;
        try {
            result = repoSystem.resolveArtifact(repoSession, request);
        } catch (ArtifactResolutionException e) {
            throw new RuntimeException("Could not resolve DevServer artifact in Maven.");
        }

        return result.getArtifact().getFile().getPath();
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

    public String getYawpVersion() {
        for (Dependency dependency : project.getDependencies()) {
            if (dependency.getGroupId().equals(YAWP_GROUP_ID)) {
                return dependency.getVersion();
            }
        }
        throw new RuntimeException("Could not resolve YAWP! version in Maven.");
    }
}