package endpoint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Target {

	Class<? extends DatastoreObject> value() default DatastoreObject.class;

}
