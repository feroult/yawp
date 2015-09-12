package io.yawp.commons.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class JsonResponse extends HttpResponse {

	private String json;

	public JsonResponse(String json) {
		this.json = json;
	}

	@Override
	public String getText() {
		return json;
	}

	@Override
	public void execute(HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json;charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		if (json != null) {
			resp.getWriter().print(json);
		}
	}

}
