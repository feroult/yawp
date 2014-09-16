package io.yawp.utils;

import java.lang.reflect.InvocationTargetException;

// TODO think about the whole expcetion model, relating to repository and servlet packages
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
