package io.yawp.repository.tools;

import io.yawp.commons.utils.Environment;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteAllServlet extends HttpServlet {

	private static final long serialVersionUID = -3346681549334024512L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (Environment.isProduction()) {
			forbidden(resp);
			return;
		}

		resp.setContentType("text/plain");
		execute(resp.getWriter());
	}

	private void execute(PrintWriter writer) {
		DeleteAll.now();
		writer.println("ok");
	}

	private void forbidden(HttpServletResponse resp) {
		resp.setStatus(403);
		return;
	}

}
