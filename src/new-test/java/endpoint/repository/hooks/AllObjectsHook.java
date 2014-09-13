package endpoint.repository.hooks;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Grandchild;
import endpoint.repository.models.parents.Parent;

public class AllObjectsHook extends Hook<Object> {

	@Override
	public void afterSave(Object object) {
		if (!isHookTest(object)) {
			return;
		}

		setName(object, "xpto");
	}

	private void setName(Object object, String name) {
		try {
			PropertyUtils.setSimpleProperty(object, "name", name);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isHookTest(Object object) {
		if (!(Parent.class.isInstance(object) || Child.class.isInstance(object) || Grandchild.class.isInstance(object))) {
			return false;
		}
		String name = getName(object);
		return object != null && name.equals("hook_test");
	}

	private String getName(Object object) {
		try {
			Object name = PropertyUtils.getProperty(object, "name");
			if (object == null) {
				return null;
			}
			return name.toString();
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
