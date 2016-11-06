package io.yawp.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.hierarchy.ObjectSubClass;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Grandchild;
import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.query.MoreThanOneResultException;
import io.yawp.repository.query.NoResultException;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOptions;
import io.yawp.repository.query.condition.BaseCondition;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static io.yawp.repository.models.basic.BasicObject.saveManyBasicObjects;
import static io.yawp.repository.models.basic.BasicObject.saveOneObject;
import static io.yawp.repository.query.condition.Condition.*;
import static org.junit.Assert.*;

public class GraphQLTest extends EndpointTestCase {

    @Test
    public void testGraphQLListAll() {
        yawp.save(new BasicObject("test"));
        String result = execute("{ basic_objects { stringValue } }").getData().toString();
        Assert.assertEquals("{basic_objects=[{stringValue=test}]}", result);
    }

    @Test
    public void testGraphQLWithFilter() {
        yawp.save(new BasicObject("test1"));
        yawp.save(new BasicObject("test2"));
        yawp.save(new BasicObject("test3"));
        String result = execute("{ basic_objects(stringValue: \"test2\") { stringValue } }").getData().toString();
        Assert.assertEquals("{basic_objects=[{stringValue=test2}]}", result);
    }

    public ExecutionResult execute(String query) {
        GraphQLObjectType q = yawp.getFeatures().generateGraphQLQuery();
        GraphQL graph = new GraphQL(GraphQLSchema.newSchema().query(q).build());
        return graph.execute(query);
    }
}
