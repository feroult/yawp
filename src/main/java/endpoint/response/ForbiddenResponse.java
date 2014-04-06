package endpoint.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ForbiddenResponse extends HttpResponse {

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void execute(HttpServletResponse resp) throws IOException {
		resp.setStatus(403);
	}

}
