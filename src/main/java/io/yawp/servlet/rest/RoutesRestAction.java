package io.yawp.servlet.rest;

public class RoutesRestAction extends RestAction {

	public RoutesRestAction() {
		super("routes");
	}

	@Override
	public String action() {
		return "{ wip : 'todo' }";
	}
}
