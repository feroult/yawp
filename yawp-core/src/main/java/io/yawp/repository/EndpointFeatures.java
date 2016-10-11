package io.yawp.repository;

import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.shields.ShieldInfo;

import graphql.schema.GraphQLObjectType;

import java.lang.reflect.Method;
import java.util.*;

public class EndpointFeatures<T> {

    private Class<T> clazz;

    private Map<ActionKey, ActionMethod> actions;

    private Map<String, Method> transformers;

    private Set<Class<? extends Hook>> hooks;

    private ShieldInfo<? super T> shieldInfo;

    private Set<Class<? extends Pipe>> pipes;

    private List<Class<? extends Pipe>> pipesSink;

    public EndpointFeatures(Class<T> clazz) {
        this.clazz = clazz;
        this.actions = new HashMap<>();
        this.transformers = new HashMap<>();
        this.hooks = new HashSet<>();
        this.pipes = new HashSet<>();
    }

    private void assertValidPathOrKind() {
        Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
        if (endpoint == null) {
            throw new RuntimeException("The class " + clazz.getName() + " was used as an entity but was not annotated with @Endpoint.");
        }
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public Endpoint getEndpointAnnotation() {
        return clazz.getAnnotation(Endpoint.class);
    }

    public String getEndpointPath() {
        Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
        return endpoint.path();
    }

    public String getEndpointKind() {
        return KindResolver.getKindFromClass(clazz);
    }


    public ActionMethod getAction(ActionKey key) {
        return actions.get(key);
    }

    public Class<?> getActionClazz(ActionKey key) {
        return actions.get(key).getMethod().getDeclaringClass();
    }

    public boolean hasCustomAction(ActionKey actionKey) {
        return actions.containsKey(actionKey);
    }

    public Method getTransformer(String name) {
        return transformers.get(name);
    }

    public boolean hasTranformer(String transformerName) {
        return transformers.containsKey(transformerName);
    }

    public Set<Class<? extends Hook>> getHooks() {
        return hooks;
    }

    public ShieldInfo<? super T> getShieldInfo() {
        return shieldInfo;
    }

    public Set<Class<? extends Pipe>> getPipes() {
        return pipes;
    }

    public List<Class<? extends Pipe>> getPipesSink() {
        return pipesSink;
    }

    public boolean hasShield() {
        return shieldInfo != null;
    }

    public void setActions(Map<ActionKey, ActionMethod> actions) {
        this.actions = actions;
    }

    public void setTransformers(Map<String, Method> transformers) {
        this.transformers = transformers;
    }

    public void setHooks(Set<Class<? extends Hook>> hooks) {
        this.hooks = hooks;
    }

    public void setShieldInfo(ShieldInfo<? super T> shieldInfo) {
        this.shieldInfo = shieldInfo;
    }

    public void setPipes(Set<Class<? extends Pipe>> pipes) {
        this.pipes = pipes;
    }

    public void setPipesSink(List<Class<? extends Pipe>> pipesSink) {
        this.pipesSink = pipesSink;
    }

    public GraphQLObjectType toObjectType() {
        return GraphQLExtractor.parse(this.getEndpointPath(), this.clazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EndpointFeatures<?> that = (EndpointFeatures<?>) o;

        return clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }
}
