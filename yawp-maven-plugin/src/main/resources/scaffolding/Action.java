package ${yawpPackage}.models.${endpoint.packageName};

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;

public class $endpoint.actionName extends Action<$endpoint.name> {

	@GET("dummy")
	public $endpoint.name dummy(IdRef<$endpoint.name> id) {
		return id.fetch();
	}

}
