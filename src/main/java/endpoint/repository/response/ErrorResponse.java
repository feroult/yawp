package endpoint.repository.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ErrorResponse extends HttpResponse {

	private int httpStatus;
	private String text;

	public ErrorResponse(int httpStatus, String text) {
		this.httpStatus = httpStatus;
		this.text = text;
	}

	public ErrorResponse(int httpStatus) {
		this(httpStatus, null);
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void execute(HttpServletResponse resp) throws IOException {
		resp.setStatus(httpStatus);
		if (text != null) {
			resp.getWriter().write(text);
		}
	}

}
