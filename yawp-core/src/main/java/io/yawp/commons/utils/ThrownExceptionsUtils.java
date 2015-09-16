package io.yawp.commons.utils;

import java.lang.reflect.InvocationTargetException;

public final class ThrownExceptionsUtils {

	private ThrownExceptionsUtils() {
		throw new RuntimeException("Should never be instanciated.");
	}

	public static RuntimeException handle(Throwable ex) {
		Throwable cause = ex;
		while (cause instanceof InvocationTargetException) {
			cause = cause.getCause();
		}

		if (cause instanceof RuntimeException) {
			return (RuntimeException) cause;
		}

		return new RuntimeException(cause);
	}
}
