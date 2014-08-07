package endpoint.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import endpoint.DatastoreException;
import endpoint.HttpException;

public final class ThrownExceptionsUtils {

	private static final List<Class<? extends RuntimeException>> ALLOWED_EXCEPTIONS = Arrays.asList(
		DatastoreException.class,
		HttpException.class
	);

	private ThrownExceptionsUtils() {
		throw new RuntimeException("Should never be instanciated.");
	}

	public static RuntimeException handle(Throwable ex) {
		Throwable cause = ex;
		while (cause instanceof InvocationTargetException) {
			cause = cause.getCause();
		}

		for (Class<? extends RuntimeException> klass : ALLOWED_EXCEPTIONS) {
			if (klass.isInstance(cause)) {
				return (RuntimeException) cause;
			}
		}

		return new RuntimeException(cause);
	}
}
