package io.yawp.repository.features.scanner;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.actions.Action;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.features.EndpointFeatures;
import io.yawp.repository.features.RepositoryFeatures;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.shields.Shield;
import io.yawp.repository.transformers.Transformer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class ClassloaderScanner {

    private final static Logger logger = Logger.getLogger(ClassloaderScanner.class.getName());

    private String packages;

    private Reflections endpointsPackage;

    private Map<Class<?>, EndpointTree<?>> trees;

    /**
     * @deprecated in 2.0 it will be internal
     */
    @Deprecated
    public ClassloaderScanner(String packages) {
        logger.finer("initializing");
        this.packages = packages;
        this.endpointsPackage = buildReflections(packages);
        this.trees = new HashMap<>();
        logger.finer("done");
    }

    private Reflections buildReflections(String packages) {
        String[] packagesArray = packages.replaceAll(" ", "").split(",");

        FilterBuilder filter = new FilterBuilder();

        Set<URL> urls = new HashSet();

        for (String packageStr : packagesArray) {
            urls.addAll(ClasspathHelper.forPackage(packageStr));
            filter.include(FilterBuilder.prefix(packageStr));
        }

        return new Reflections(new ConfigurationBuilder()
                .addUrls(urls)
                .filterInputsBy(filter)
                .setScanners(new TypeAnnotationsScanner(), new SubTypesScanner()));
    }

    public RepositoryFeatures scan() {
        long start = System.currentTimeMillis();
        RepositoryFeatures repositoryFeatures = new RepositoryFeatures(scanAndLoadAll());
        long elapsed = System.currentTimeMillis() - start;
        logger.info("YAWP! started in " + elapsed + " ms - package: " + packages);
        return repositoryFeatures;
    }

    private Map<Class<?>, EndpointFeatures<?>> scanAndLoadAll() {
        scanEndpoints();
        scanFeatures();
        return loadAll();
    }

    private void scanFeatures() {
        scanActions();
        scanTransformers();
        scanPipes();
        scanHooks();
        scanShields();
    }

    private Map<Class<?>, EndpointFeatures<?>> loadAll() {
        Map<Class<?>, EndpointFeatures<?>> endpoints = new HashMap<>();

        ActionLoader actionLoader = new ActionLoader();
        TransformerLoader transformerLoader = new TransformerLoader();

        for (Class<?> endpointClazz : trees.keySet()) {
            EndpointTree<?> tree = trees.get(endpointClazz);
            EndpointFeatures<?> endpoint = new EndpointFeatures<>(endpointClazz);

            actionLoader.load(endpoint, tree);
            transformerLoader.load(endpoint, tree);
            endpoint.setHooks(tree.loadHooks());
            endpoint.setShieldInfo(tree.loadShield());
            endpoint.setPipes(tree.loadPipes());
            endpoint.setPipesSink(tree.loadPipesSink());

            endpoints.put(endpointClazz, endpoint);
        }

        return endpoints;
    }

    private void scanEndpoints() {
        Set<Class<?>> userClazzes = endpointsPackage.getTypesAnnotatedWith(Endpoint.class);

        for (Class<?> endpointClazz : userClazzes) {
            trees.put(endpointClazz, new EndpointTree(endpointClazz));
        }
    }

    private List<Class<?>> findEndpointsInHierarchy(Class<?> parameterClazz, Class<?> featureClazz) {
        List<Class<?>> clazzes = new ArrayList<>();
        for (Class<?> endpointClazz : trees.keySet()) {
            if (isEndpointInTheHierarchy(endpointClazz, parameterClazz)) {
                clazzes.add(endpointClazz);
            }
        }
        if (clazzes.isEmpty()) {
            throw new RuntimeException("Tryed to create feature '" + featureClazz.getName() + "' with entity '" + parameterClazz.getName()
                    + "' that is not an @Endpoint nor a super class of one.");
        }
        return clazzes;
    }

    private <T> boolean isEndpointInTheHierarchy(Class<?> endpoint, Class<T> objectClazz) {
        return objectClazz.isAssignableFrom(endpoint);
    }

    private void scanActions() {
        Set<Class<? extends Action>> clazzes = endpointsPackage.getSubTypesOf(Action.class);

        for (Class<? extends Action> actionClazz : clazzes) {
            if (Modifier.isAbstract(actionClazz.getModifiers())) {
                continue;
            }

            addActionToEndpoints(actionClazz);
        }
    }

    private void addActionToEndpoints(Class<? extends Action> actionClazz) {
        Class<?> parameterClazz = ReflectionUtils.getFeatureEndpointClazz(actionClazz);

        if (parameterClazz == null) {
            return;
        }

        for (Class<?> endpointClazz : findEndpointsInHierarchy(parameterClazz, actionClazz)) {
            trees.get(endpointClazz).addAction(actionClazz);
        }
    }

    private void scanTransformers() {
        Set<Class<? extends Transformer>> clazzes = endpointsPackage.getSubTypesOf(Transformer.class);

        for (Class<? extends Transformer> transformerClazz : clazzes) {
            if (Modifier.isAbstract(transformerClazz.getModifiers())) {
                continue;
            }

            addTransformerToEndpoints(transformerClazz);
        }
    }

    private void addTransformerToEndpoints(Class<? extends Transformer> transformerClazz) {
        Class<?> parameterClazz = ReflectionUtils.getFeatureEndpointClazz(transformerClazz);

        if (parameterClazz == null) {
            return;
        }

        for (Class<?> endpointClazz : findEndpointsInHierarchy(parameterClazz, transformerClazz)) {
            trees.get(endpointClazz).addTransformer(transformerClazz);
        }
    }

    private void scanHooks() {
        Set<Class<? extends Hook>> clazzes = endpointsPackage.getSubTypesOf(Hook.class);

        for (Class<? extends Hook> hookClazz : clazzes) {
            if (Modifier.isAbstract(hookClazz.getModifiers())) {
                continue;
            }

            addHookToEndpoints(hookClazz);
        }
    }

    private <T, V extends Hook<T>> void addHookToEndpoints(Class<V> hookClazz) {
        Class<T> parameterClazz = (Class<T>) ReflectionUtils.getFeatureEndpointClazz(hookClazz);

        if (parameterClazz == null) {
            return;
        }

        for (Class<?> endpointClazz : findEndpointsInHierarchy(parameterClazz, hookClazz)) {
            trees.get(endpointClazz).addHook(hookClazz);
        }
    }

    private void scanShields() {
        Set<Class<? extends Shield>> clazzes = endpointsPackage.getSubTypesOf(Shield.class);

        for (Class<? extends Shield> shieldClazz : clazzes) {
            if (Modifier.isAbstract(shieldClazz.getModifiers())) {
                continue;
            }

            addShieldToEndpoints(shieldClazz);
        }
    }

    private <T, V extends Shield<T>> void addShieldToEndpoints(Class<V> shieldClazz) {
        Class<T> parameterClazz = (Class<T>) ReflectionUtils.getFeatureEndpointClazz(shieldClazz);

        if (parameterClazz == null) {
            return;
        }

        for (Class<?> endpointClazz : findEndpointsInHierarchy(parameterClazz, shieldClazz)) {
            trees.get(endpointClazz).addShield(shieldClazz);
        }
    }

    private void scanPipes() {
        Set<Class<? extends Pipe>> clazzes = endpointsPackage.getSubTypesOf(Pipe.class);

        for (Class<? extends Pipe> pipeClazz : clazzes) {
            if (Modifier.isAbstract(pipeClazz.getModifiers())) {
                continue;
            }

            addPipeToEndpoints(pipeClazz);
        }
    }

    private <T, S, V extends Pipe<T, S>> void addPipeToEndpoints(Class<V> pipeClazz) {
        Class<T> sourceClazz = (Class<T>) ReflectionUtils.getFeatureEndpointClazz(pipeClazz);

        if (sourceClazz == null) {
            return;
        }

        for (Class<?> endpointClazz : findEndpointsInHierarchy(sourceClazz, pipeClazz)) {
            trees.get(endpointClazz).addPipe(pipeClazz);
        }

        Class<S> sinkClazz = (Class<S>) ReflectionUtils.getFeatureTypeArgumentAt(pipeClazz, 1);
        trees.get(sinkClazz).addPipeSink(pipeClazz);
    }

}
