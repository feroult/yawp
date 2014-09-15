package endpoint.servlet.misc;

import org.junit.Test;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.servlet.HttpException;
import endpoint.servlet.ServletTestCase;

public class RestrictionsTest extends ServletTestCase {

	@Test
	public void testIndexAllowed() {
		get("/basic_objects");
	}

	@Test(expected = HttpException.class)
	public void testIndexNotAllowed() {
		get("/endpoints_without_index");
	}

	@Endpoint(path = "/endpoints_without_index", index = false)
	public class EndpointWithoutIndex {
		private IdRef<EndpointWithoutIndex> id;

		public IdRef<EndpointWithoutIndex> getId() {
			return id;
		}

		public void setId(IdRef<EndpointWithoutIndex> id) {
			this.id = id;
		}
	}
}
