package io.yawp.repository.actions;

import io.yawp.commons.http.annotation.GET;
import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.parents.Parent;

import java.util.List;
import java.util.Map;

public class ParentAction extends Action<Parent> {

	@PUT("touched")
	public Parent touch(IdRef<Parent> id) {
		Parent parent = id.fetch();
		parent.setName("touched " + parent.getName());
		return parent;
	}

	@PUT("touchedParams")
	public Parent touchedParams(IdRef<Parent> id, Map<String, String> params) {
		Parent parent = id.fetch();
		parent.setName("touched " + parent.getName() + " by " + params.get("arg"));
		return parent;
	}

	@PUT("touched")
	public List<Parent> touch() {
		List<Parent> parents = yawp(Parent.class).order("name").list();
		for (Parent parent : parents) {
			parent.setName("touched " + parent.getName());
		}
		return parents;
	}

	@GET("something")
	public String something() {
		return "touched";
	}

	@PUT("touched_with_params")
	public Parent touchWithParams(IdRef<Parent> id, Map<String, String> params) {
		Parent parent = id.fetch();
		parent.setName("touched " + parent.getName() + " " + params.get("x"));
		return parent;
	}

	@PUT("touched_with_params")
	public List<Parent> touchWithParams(Map<String, String> params) {
		List<Parent> parents = yawp(Parent.class).list();
		for (Parent parent : parents) {
			parent.setName("touched " + parent.getName() + " " + params.get("x"));
		}
		return parents;
	}

	@GET("echo")
	public Parent echo(IdRef<Parent> id) {
		return id.fetch();
	}

	@Atomic
	@PUT("atomic_rollback")
	public void atomicRollback() {
		yawp.save(new Parent("xpto"));
		throw new FakeException();
	}
}
