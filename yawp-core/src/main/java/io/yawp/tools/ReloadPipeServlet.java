package io.yawp.tools;

import io.yawp.driver.api.Driver;
import io.yawp.driver.api.DriverFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static io.yawp.repository.Yawp.feature;

public class ReloadPipeServlet extends HttpServlet {

    private static final long serialVersionUID = -3346681549334024512L;
    public static final String PIPE_PARAM = "pipe";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");

        if (req.getParameter(PIPE_PARAM) == null) {
            error(resp);
            return;
        }

        execute(resp.getWriter(), req.getParameter(PIPE_PARAM));
    }

    private void error(HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        writer.println("use servlet?pipe=pipe=class-name");
        resp.setStatus(403);
    }

    private boolean enable() {
        Driver driver = DriverFactory.getDriver();
        return driver.environment().isAdmin();
    }

    private void execute(PrintWriter writer, String pipeClazzName) {
        feature(ReloadPipe.class).now(pipeClazzName);
        writer.println("ok");
    }
    
}
