package io.yawp.servlet.misc;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.servlet.HttpException;
import io.yawp.servlet.ServletTestCase;

import org.junit.Test;

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
