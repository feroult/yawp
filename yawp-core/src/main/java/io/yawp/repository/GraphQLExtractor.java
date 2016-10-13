package io.yawp.repository;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import io.yawp.commons.utils.ReflectionUtils;

public class GraphQLExtractor<T> {

    public static GraphQLObjectType parse(String name, Class<?> clazz) {
        GraphQLObjectType.Builder b = newObject().name(name);
        for (Field f : ReflectionUtils.getFieldsRecursively(clazz)) {
            b.field(newFieldDefinition().name(f.getName()).type(resolveType(f)).dataFetcher(fetcher(f)).build());
        }
        return b.build();
    }

    private static GraphQLScalarType resolveType(Field f) {
        return GraphQLString;
    }

    private static DataFetcher fetcher(final Field f) {
        return new DataFetcher() {

            @Override
            public Object get(DataFetchingEnvironment environment) {
                f.setAccessible(true);
                try {
                    return f.get(environment.getSource());
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException("Should never be thrown", e);
                }
            }
        };
    }

    public static List<GraphQLArgument> args(Class<?> clazz) {
        List<Field> fields = ReflectionUtils.getFieldsRecursively(clazz);
        List<GraphQLArgument> args = new ArrayList<>(fields.size());
        for (Field f : fields) {
            args.add(new GraphQLArgument(f.getName(), resolveType(f)));
        }
        return args;
    }
}
