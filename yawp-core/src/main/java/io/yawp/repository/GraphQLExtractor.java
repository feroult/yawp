package io.yawp.repository;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

public class GraphQLExtractor<T> {

    public static GraphQLObjectType parse(String name, Class<?> clazz) {
        return newObject()
            .name(name)
            .field(newFieldDefinition()
                .name("test")
                .type(GraphQLString).build())
            .build();
    }
}
