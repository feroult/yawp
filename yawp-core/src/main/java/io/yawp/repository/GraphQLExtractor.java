package io.yawp.repository;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.lang.reflect.Field;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import io.yawp.commons.utils.ReflectionUtils;

public class GraphQLExtractor<T> {

	public static GraphQLObjectType parse(String name, Class<?> clazz) {
		GraphQLObjectType.Builder b = newObject().name(name);
		for (Field f : ReflectionUtils.getFieldsRecursively(clazz)) {
			b.field(newFieldDefinition().name(f.getName()).type(GraphQLString).dataFetcher(fetcher(f)).build());
		}
		return b.build();
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
}
