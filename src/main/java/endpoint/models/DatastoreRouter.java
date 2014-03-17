package endpoint.models;

import endpoint.models.actions.ActionType;

public class DatastoreRouter {

	private String method;

	private String path;

	private ActionType action;

	private String customAction;

	private Long id;

	public DatastoreRouter(String method, String path) {
		this.method = method;
		this.path = path;
		parse();
	}

	private void parse() {
		parsePath();
		parseMethod();

		if (action == null) {
			throw new IllegalArgumentException("Invalid datastore action");
		}
	}

	private void parsePath() {
		String[] parts = path.split("/");

		// /api/devices/100
		if (parts.length >= 4) {
			id = Long.valueOf(parts[3]);
		}

		// /api/devices/100/active
		if (parts.length == 5) {
			customAction = parts[4];
		}
	}

	private void parseMethod() {
		if (customAction != null) {
			action = ActionType.CUSTOM;
			return;
		}

		if (method.equals("GET")) {
			if (id == null) {
				action = ActionType.INDEX;
			} else {
				action = ActionType.SHOW;
			}

			return;
		}

		if (method.equals("POST")) {
			action = ActionType.CREATE;
			return;
		}

		if (method.equals("PUT")) {
			action = ActionType.UPDATE;
			return;
		}
	}

	public ActionType getAction() {
		return action;
	}

	public Long getId() {
		return id;
	}

	public String getCustomAction() {
		return customAction;
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}
}
