package ${package}.models.${endpoint.name};

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;

@Endpoint(path = "/$endpoint.path")
public class $endpoint.clazz {

	@Id
	private IdRef<${endpoint.clazz}> id;

	public IdRef<$endpoint.clazz> getId() {
		return id;
	}

	public void setId(IdRef<$endpoint.clazz> id) {
		this.id = id;
	}

}
