package io.yawp.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import graphql.GraphQL;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.Yawp;

public class GraphQLServlet extends HttpServlet {

    private static final long serialVersionUID = 8817254175731665140L;

    public GraphQLServlet() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = JsonUtils.readJson(req.getReader());
        resp.getWriter().write(response(body).toString());
        resp.getWriter().flush();
        resp.getWriter().close();
        resp.setStatus(200);
    }

    private String response(String input) {
        GraphQLObjectType query = Yawp.yawp().getFeatures().generateGraphQLQuery();
        GraphQL graph = new GraphQL(GraphQLSchema.newSchema().query(query).build());
        return graph.execute(input).getData().toString();
    }

}
