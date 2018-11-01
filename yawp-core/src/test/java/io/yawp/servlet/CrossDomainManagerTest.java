package io.yawp.servlet;

import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static io.yawp.servlet.CrossDomainManager.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CrossDomainManagerTest {

	private static abstract class MockedServletConfig implements ServletConfig {
		@Override
		public String getServletName() {
			return null;
		}

		@Override
		public ServletContext getServletContext() {
			return null;
		}

		@Override
		public Enumeration getInitParameterNames() {
			return null;
		}
	}

	@Test
	public void createWithoutInitForMocks() {
		Map<String, String> data = new HashMap<>();
		data.put("enableCrossDomain", "true");
		data.put("origin", "?");
		data.put("methods", DEFAULT_METHODS);
		data.put("headers", DEFAULT_HEADERS);
		data.put("allowCredentials", "false");
		data.put("exposeHeaders", "Auth-Cache");
		CrossDomainManager cors = new CrossDomainManager(data);

		assertThat(cors.isEnableCrossDomain(), is(equalTo(true)));
		assertThat(cors.getOrigin(), is(equalTo("?")));
		assertThat(cors.getMethods(), is(equalTo("GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD")));
		assertThat(cors.getHeaders(), is(equalTo("Origin, X-Requested-With, Content-Type, Accept, Authorization, namespace")));
		assertThat(cors.getAllowCredentials(), is(equalTo("false")));
		assertThat(cors.getExposeHeaders(), is(equalTo("Auth-Cache")));
	}

	@Test
	public void createWithEmptyConfigDefaultValues() {
		CrossDomainManager cors = new CrossDomainManager();
		cors.init(new MockedServletConfig() {
			@Override
			public String getInitParameter(String name) {
				return null;
			}
		});

		assertThat(cors.isEnableCrossDomain(), is(equalTo(true)));
		assertThat(cors.getOrigin(), is(equalTo(DEFAULT_ORIGIN)));
		assertThat(cors.getMethods(), is(equalTo(DEFAULT_METHODS)));
		assertThat(cors.getHeaders(), is(equalTo(DEFAULT_HEADERS)));
		assertThat(cors.getAllowCredentials(), is(equalTo(DEFAULT_ALLOW_CREDENTIALS)));
		assertThat(cors.getExposeHeaders(), is(equalTo(DEFAULT_EXPOSE_HEADERS)));
	}

	@Test
	public void createWithFullConfig() {
		CrossDomainManager cors = new CrossDomainManager();
		cors.init(new MockedServletConfig() {
			@Override
			public String getInitParameter(String name) {
				switch (name) {
					case "enableCrossDomain":
						return "true";
					case "crossDomainOrigin":
						return "site.com";
					case "crossDomainMethods":
						return "GET, POST";
					case "crossDomainHeaders":
						return null;
					case "crossDomainAllowCredentials":
						return "false";
					case "crossDomainExposeHeaders":
						return null;
					default:
						return null;
				}
			}
		});

		assertThat(cors.isEnableCrossDomain(), is(equalTo(true)));
		assertThat(cors.getOrigin(), is(equalTo("site.com")));
		assertThat(cors.getMethods(), is(equalTo("GET, POST")));
		assertThat(cors.getHeaders(), is(equalTo(null)));
		assertThat(cors.getAllowCredentials(), is(equalTo("false")));
		assertThat(cors.getExposeHeaders(), is(equalTo(null)));
	}
}
