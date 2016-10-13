package io.yawp.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
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
        ExecutionResult result = executeQuery(body);
        parseResult(resp, result);
    }

    private void parseResult(HttpServletResponse resp, ExecutionResult result) throws IOException {
        if (result.getErrors().isEmpty()) {
            writeResponse(resp, 200, result.getData().toString());
        } else {
            ((ExceptionWhileDataFetching) result.getErrors().get(0)).getException().printStackTrace();
            writeResponse(resp, 500, result.getErrors().toString());
        }
    }

    private ExecutionResult executeQuery(String body) {
        GraphQLObjectType query = Yawp.yawp().getFeatures().generateGraphQLQuery();
        GraphQL graph = new GraphQL(GraphQLSchema.newSchema().query(query).build());
        ExecutionResult result = graph.execute(body);
        return result;
    }

    private void writeResponse(HttpServletResponse resp, int sc, String response) throws IOException {
        resp.getWriter().write(response);
        resp.getWriter().flush();
        resp.getWriter().close();
        resp.setStatus(sc);
    }
}
