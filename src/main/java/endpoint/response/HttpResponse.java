package endpoint.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public abstract class HttpResponse {

	public abstract String getText();

	public abstract void execute(HttpServletResponse resp) throws IOException;

}
