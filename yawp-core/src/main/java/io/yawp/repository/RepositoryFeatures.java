package io.yawp.repository;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import io.yawp.commons.http.HttpVerb;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.shields.Shield;
import io.yawp.repository.shields.ShieldInfo;

public class RepositoryFeatures {

    private Map<Class<?>, EndpointFeatures<?>> endpoints;

    private Map<String, Class<?>> paths;

    private Map<String, Class<?>> kinds;

    protected RepositoryFeatures() {
    }

    public RepositoryFeatures(Map<Class<?>, EndpointFeatures<?>> endpoints) {
        this.endpoints = endpoints;
        this.paths = new HashMap<>();
        this.kinds = new HashMap<>();
        initAndLoadPaths();
    }

    private void initAndLoadPaths() {
        for (EndpointFeatures<?> endpoint : endpoints.values()) {
            addKindToMap(endpoint);
            addMapPathKind(endpoint);
        }
    }

    private void addKindToMap(EndpointFeatures<?> endpoint) {
        String kind = endpoint.getEndpointKind();
        kinds.put(kind, endpoint.getClazz());
    }

    private void addMapPathKind(EndpointFeatures<?> endpoint) {
        String endpointPath = endpoint.getEndpointPath();
        if (endpointPath.isEmpty()) {
            return;
        }
        assertIsValidPath(endpoint, endpointPath);
        paths.put(endpointPath, endpoint.getClazz());
    }

    private void assertIsValidPath(EndpointFeatures<?> endpoint, String endpointPath) {
        if (paths.get(endpointPath) != null) {
            throw new RuntimeException("Repeated io.yawp path " + endpointPath + " for class " + endpoint.getClazz().getSimpleName()
                    + " (already found in class " + paths.get(endpointPath).getSimpleName() + ")");
        }
        if (!isValidEndpointPath(endpointPath)) {
            throw new RuntimeException("Invalid endpoint path " + endpointPath + " for class " + endpoint.getClazz().getSimpleName());
        }
    }

    protected boolean isValidEndpointPath(String endpointName) {
        char[] charArray = endpointName.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (i == 0) {
                if (c != '/') {
                    return false;
                }
                continue;
            }
            if (!(Character.isAlphabetic(c) || c == '_' || c == '-')) {
                return false;
            }
        }
        return true;
    }

    public EndpointFeatures<?> getByClazz(Class<?> clazz) {
        return endpoints.get(clazz);
    }

    public EndpointFeatures<?> getByPath(String endpointPath) {
        Class<?> clazz = paths.get(endpointPath);
        if (clazz == null) {
            throw new EndpointNotFoundException(endpointPath);
        }
        return getByClazz(clazz);
    }

    public Class<?> getClazzByKind(String kind) {
        return kinds.get(kind);
    }

    public boolean hasCustomAction(String endpointPath, ActionKey actionKey) {
        EndpointFeatures<?> endpointFeatures = getByPath(endpointPath);
        return endpointFeatures.hasCustomAction(actionKey);
    }

    public boolean hasCustomAction(Class<?> clazz, ActionKey actionKey) {
        EndpointFeatures<?> endpointFeatures = getByClazz(clazz);
        return endpointFeatures.hasCustomAction(actionKey);
    }

    public Set<Class<?>> getEndpointClazzes() {
        return endpoints.keySet();
    }

    public GraphQLObjectType generateGraphQLQuery() {
        GraphQLObjectType.Builder es = newObject().name("endpoints");
        for (EndpointFeatures<?> e : endpoints.values()) {
            if (!e.getEndpointKind().startsWith("__yawp")) {
                String name = e.getEndpointKind();
                GraphQLList type = new GraphQLList(e.toObjectType());
                DataFetcher fetcher = fetcher(e.getClazz());
                List<GraphQLArgument> args = e.toArgumentList();
                es.field(newFieldDefinition().name(name).type(type).dataFetcher(fetcher).argument(args).build());
            }
        }
        return es.build();
    }

    public Shield<?> defineShield(Class<?> clazz) {
        EndpointFeatures<?> endpointFeatures = Yawp.yawp().getEndpointFeatures(clazz);
        if (endpointFeatures.hasShield()) {
            return createShield(endpointFeatures);
        }
        return null;
    }

    private Shield<?> createShield(EndpointFeatures<?> endpointFeatures) {
        ShieldInfo<?> shieldInfo = endpointFeatures.getShieldInfo();

        Shield<?> shield = shieldInfo.newInstance();
        shield.setRepository(Yawp.yawp());
        shield.setEndpointClazz(endpointFeatures.getClazz());
        // shield.setActionKey(new ActionKey(HttpVerb.GET, null, false));
        shield.setActionMethods(shieldInfo.getActionMethods());
        return shield;
    }

    private DataFetcher fetcher(final Class<?> clazz) {
        return new DataFetcher() {
            @Override
            public List<?> get(DataFetchingEnvironment env) {
                Shield<?> shield = defineShield(clazz);
                shield.protectIndex();

                QueryBuilder<?> query = Yawp.yawp(clazz);
                for (String arg : env.getArguments().keySet()) {
                    Object val = env.getArgument(arg);
                    if (val != null) {
                        query.where(arg, "=", val);
                    }
                }
                if (shield.hasCondition()) {
                    query.where(shield.getWhere());
                }
                return query.list();
            }
        };
    }

}
