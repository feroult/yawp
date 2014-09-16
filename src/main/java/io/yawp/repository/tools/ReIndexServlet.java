package io.yawp.repository.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserServiceFactory;

public class ReIndexServlet extends HttpServlet {

	private static final long serialVersionUID = -3346681549334024512L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!UserServiceFactory.getUserService().isUserAdmin()) {
			forbidden(resp);
			return;
		}

		resp.setContentType("text/plain");
		execute(getPath(req), resp.getWriter());
	}

	private void execute(String path, PrintWriter writer) {
		List<Long> ids = ReIndex.parse(path).now();
		writer.print("Total re-indexed entities: " + ids.size());
	}

	private String getPath(HttpServletRequest req) {
		return req.getRequestURI().substring(req.getServletPath().length());
	}

	private void forbidden(HttpServletResponse resp) {
		resp.setStatus(403);
		return;
	}

}
