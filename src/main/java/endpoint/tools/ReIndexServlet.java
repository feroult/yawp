package endpoint.tools;

import java.io.IOException;

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

		ReIndex.parse(getPath(req)).now();
	}

	private String getPath(HttpServletRequest req) {
		return req.getRequestURI().substring(req.getServletPath().length());
	}

	private void forbidden(HttpServletResponse resp) {
		resp.setStatus(403);
		return;
	}

}
