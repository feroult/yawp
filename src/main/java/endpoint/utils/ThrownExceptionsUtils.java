package endpoint.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import endpoint.repository.EndpointException;
import endpoint.servlet.HttpException;

public final class ThrownExceptionsUtils {

	private static final List<Class<? extends RuntimeException>> ALLOWED_EXCEPTIONS = Arrays
			.<Class<? extends RuntimeException>> asList(EndpointException.class);

	private ThrownExceptionsUtils() {
		throw new RuntimeException("Should never be instanciated.");
	}

	public static HttpException handle(Throwable ex) {
		Throwable cause = ex;
		while (cause instanceof InvocationTargetException) {
			cause = cause.getCause();
		}

		if (cause instanceof HttpException) {
			return (HttpException) cause;
		}

		for (Class<? extends RuntimeException> klass : ALLOWED_EXCEPTIONS) {
			if (klass.isInstance(cause)) {
				throw (RuntimeException) cause;
			}
		}

		throw new RuntimeException(cause);
	}
}
