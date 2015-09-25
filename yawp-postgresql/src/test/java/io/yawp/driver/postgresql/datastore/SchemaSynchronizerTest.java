package io.yawp.driver.postgresql.datastore;

import io.yawp.repository.EndpointScanner;
import io.yawp.repository.IdRef;
import io.yawp.repository.RepositoryFeatures;
import io.yawp.repository.annotations.Endpoint;

import org.junit.Test;

public class SchemaSynchronizerTest extends PGDatastoreTestCase {

	@Endpoint(path = "/people")
	public class Person {

		private IdRef<Person> id;

		public IdRef<Person> getId() {
			return id;
		}

		public void setId(IdRef<Person> id) {
			this.id = id;
		}

	}

	@Test
	public void testCreateTables() {
		RepositoryFeatures features = new EndpointScanner(testPackage()).scan();

		SchemaSynchronizer.sync(features.getEndpointClazzes());

	}

	private String testPackage() {
		return getClass().getPackage().getName();
	}

}
