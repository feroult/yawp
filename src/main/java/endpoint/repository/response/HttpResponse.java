package endpoint.repository.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

// TODO think about if repository layer needs to know about http stuff
public abstract class HttpResponse {

	public abstract String getText();

	public abstract void execute(HttpServletResponse resp) throws IOException;

}
