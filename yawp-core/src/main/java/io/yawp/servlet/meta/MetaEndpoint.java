package io.yawp.servlet.meta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.EndpointFeatures;
import io.yawp.repository.actions.ActionKey;

public class MetaEndpoint {

	private String kind;
	private List<MetaField> fields;
	private List<ActionKey> actions;

	public MetaEndpoint(EndpointFeatures<?> endpoint) {
		this.kind = KindResolver.getKindFromClass(endpoint.getClazz());
		this.fields = new ArrayList<>();
		for (Field f : ReflectionUtils.getFieldsRecursively(endpoint.getClazz())) {
			this.fields.add(new MetaField(f));
		}
		this.actions = new ArrayList<>(endpoint.getActions().keySet());
	}

	public String getKind() {
		return kind;
	}

	public List<MetaField> getFields() {
		return fields;
	}

	public List<ActionKey> getActions() {
		return actions;
	}
}
