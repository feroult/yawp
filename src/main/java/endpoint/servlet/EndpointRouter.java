package endpoint.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import endpoint.repository.EndpointFeatures;
import endpoint.repository.IdRef;
import endpoint.repository.Repository;
import endpoint.repository.RepositoryFeatures;
import endpoint.repository.actions.ActionKey;
import endpoint.repository.annotations.Endpoint;
import endpoint.utils.EntityUtils;
import endpoint.utils.HttpVerb;

public class EndpointRouter {

	private Repository r;

	private RepositoryFeatures features;

	private String uri;

	private List<RouteResource> resources;

	private boolean overCollection;

	private ActionKey customActionKey;

	private HttpVerb verb;

	private IdRef<?> idRef;

	public EndpointRouter(Repository r, HttpVerb verb, String uri) {
		this.verb = verb;
		this.uri = uri;
		this.r = r;
		this.features = r.getFeatures();
		parseUri();
		validateRestrictions();
	}

	public static EndpointRouter parse(Repository r, HttpVerb verb, String uri) {
		return new EndpointRouter(r, verb, uri);
	}

	public List<RouteResource> getResources() {
		return resources;
	}

	private void parseUri() {
		this.idRef = IdRef.parse(r, uri);

		String[] parts = normalizeUri(uri).split("/");

		this.customActionKey = parseCustomActionKeyNew(parts);
		this.overCollection = parseOverCollectionNew(parts);
		this.resources = parseResources(parts);
	}

	private ActionKey parseCustomActionKeyNew(String[] parts) {

		if (idRef == null) {
			int indexOfSlash = uri.substring(1).indexOf("/");
			if (indexOfSlash == -1) {
				return null;
			}
		}

		return parseCustomActionKey(parts);
	}

	private boolean parseOverCollectionNew(String[] parts) {
		if (idRef == null) {
			return true;
		}

		if (idRef.getUri().length() == uri.length()) {
			return false;
		}

		String lastToken = uri.substring(idRef.getUri().length() + 1);
		if (lastToken.indexOf("/") != -1) {
			return true;
		}

		ActionKey actionKey = new ActionKey(verb, lastToken, false);
		if (features.hasCustomAction(idRef.getClazz(), actionKey)) {
			return false;
		}

		return true;

		// return parseOverCollection(parts);
	}

	private ActionKey parseCustomActionKey(String[] parts) {
		// /objects
		if (parts.length < 2) {
			return null;
		}

		String possibleAction = parts[parts.length - 1];

		// /objects/1
		if (!isString(possibleAction)) {
			return null;
		}

		// /objects/action
		if (parts.length % 2 == 0) {
			return parseCustomActionKeyForEvenParts(parts, possibleAction);
		}

		// /objects/1/action
		return parseCustomActionKeyForOddParts(parts, possibleAction);
	}

	private ActionKey parseCustomActionKeyForOddParts(String[] parts, String possibleAction) {
		String possiblePath = parts[parts.length - 3];
		ActionKey actionKey = new ActionKey(verb, possibleAction, false);
		if (features.hasCustomAction("/" + possiblePath, actionKey)) {
			return actionKey;
		}
		return null;
	}

	private ActionKey parseCustomActionKeyForEvenParts(String[] parts, String possibleAction) {
		String possiblePath = parts[parts.length - 2];
		if (!isString(possiblePath)) {
			return null;
		}

		ActionKey actionKey = new ActionKey(verb, possibleAction, true);
		if (features.hasCustomAction("/" + possiblePath, actionKey)) {
			return actionKey;
		}
		return null;
	}

	private ArrayList<RouteResource> parseResources(String[] parts) {
		ArrayList<RouteResource> resources = new ArrayList<RouteResource>();

		String[] resourceParts = parts;
		if (isCustomAction()) {
			resourceParts = Arrays.copyOf(parts, parts.length - 1);
		}

		for (int i = 0; i < resourceParts.length / 2; i++) {
			String path = "/" + resourceParts[i * 2];
			Long id = Long.valueOf(resourceParts[i * 2 + 1]);
			resources.add(new RouteResource(path, id));
		}

		if (resourceParts.length % 2 == 1) {
			String endpointPath = "/" + resourceParts[resourceParts.length - 1];
			resources.add(new RouteResource(endpointPath));
		}

		return resources;
	}

	private boolean parseOverCollection(String[] parts) {
		if (parts.length == 1) {
			return true;
		}
		if (!isString(parts[parts.length - 1])) {
			return false;
		}
		if (!isCustomAction()) {
			return true;
		}
		return isString(parts[parts.length - 2]);
	}

	private boolean isString(String lastToken) {
		try {
			Long.valueOf(lastToken);
			return false;
		} catch (NumberFormatException e) {
			return true;
		}
	}

	public boolean isOverCollection() {
		return overCollection;
	}

	public boolean isCustomAction() {
		return customActionKey != null;
	}

	public String getCustomActionName() {
		if (!isCustomAction()) {
			return null;
		}
		return customActionKey.getActionName();
	}

	public ActionKey getCustomActionKey() {
		return customActionKey;
	}

	private String getEndpointPath() {
		return resources.get(resources.size() - 1).getEndpointPath();
	}

	public EndpointFeatures<?> getEndpointFeatures() {
		return features.get(getEndpointPath());
	}

	public Class<?> getEndpointClazz() {
		return getEndpointFeatures().getClazz();
	}

	public IdRef<?> getIdRef() {
		if (isOverCollection()) {
			return null;
		}
		return idRef;
	}

	public IdRef<?> getParentIdRef() {
		if (idRef == null) {
			return null;
		}

		if (isOverCollection()) {
			return idRef;
		}

		return idRef.getParentId();
	}

	public IdRef<?> getActionIdRef() {
		return idRef;
		// if (isOverCollection()) {
		// IdRef<?> actionIdRef = IdRef.create(r,
		// EntityUtils.getIdType(getEndpointClazz()), (Long) null);
		// actionIdRef.setParentId(idRef);
		// return actionIdRef;
		// }
		// return idRef;
	}

	public RESTActionType getRESTActionType() {
		if (isCustomAction()) {
			return RESTActionType.CUSTOM;
		}
		return RESTActionType.defaultRestAction(verb, isOverCollection());
	}

	private void validateRestrictions() {
		Endpoint endpointAnnotation = getEndpointFeatures().getEndpointAnnotation();
		getRESTActionType().validateRetrictions(endpointAnnotation);
	}

	protected class RouteResource {

		private String endpointPath;

		private Long id;

		public RouteResource(String endpointPath) {
			this(endpointPath, null);
		}

		public RouteResource(String endpointPath, Long id) {
			this.endpointPath = endpointPath;
			this.id = id;
		}

		public String getEndpointPath() {
			return endpointPath;
		}

		public IdRef<?> getIdRef(IdRef<?> parentId) {
			if (id == null) {
				return parentId;
			}
			Class<?> clazz = features.get(endpointPath).getClazz();
			IdRef<?> idRef = IdRef.create(r, EntityUtils.getIdType(clazz), id);
			idRef.setParentId(parentId);
			return idRef;
		}

		protected Long getId() {
			return id;
		}
	}

	private String normalizeUri(String uri) {
		return normalizeUriEnd(normalizeUriStart(uri));
	}

	private String normalizeUriStart(String uri) {
		if (uri.charAt(0) == '/') {
			return uri.substring(1);
		}
		return uri;
	}

	private String normalizeUriEnd(String uri) {
		if (uri.charAt(uri.length() - 1) == '/') {
			return uri.substring(0, uri.length() - 1);
		}
		return uri;
	}
}
