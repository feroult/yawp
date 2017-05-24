package io.yawp.repository.hooks;

import io.yawp.commons.utils.ThrownExceptionsUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.query.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class RepositoryHooks {

    private final static Logger logger = Logger.getLogger(RepositoryHooks.class.getName());

    private RepositoryHooks() {
    }

    public static void beforeShield(Repository r, Object object) {
        invokeHooks(r, object.getClass(), object, "beforeShield");
    }

    public static void beforeSave(Repository r, Object object) {
        invokeHooks(r, object.getClass(), object, "beforeSave");
    }

    public static void afterSave(Repository r, Object object) {
        invokeHooks(r, object.getClass(), object, "afterSave");
    }

    public static <T> void beforeQuery(Repository r, QueryBuilder<T> q, Class<T> clazz) {
        invokeHooks(r, clazz, q, "beforeQuery");
    }

    public static void beforeDestroy(Repository r, IdRef<?> id) {
        invokeHooks(r, id.getClazz(), id, "beforeDestroy");
    }

    public static void afterDestroy(Repository r, IdRef<?> id) {
        invokeHooks(r, id.getClazz(), id, "afterDestroy");
    }

    private static void invokeHooks(Repository r, Class<?> targetClazz, Object argument, String methodName) {
        for (Class<? extends Hook> hookClazz : r.getEndpointFeatures(targetClazz).getHooks()) {
            logHookFound(argument, hookClazz);
            invokeHookMethod(r, hookClazz, methodName, argument);
        }
    }

    private static void invokeHookMethod(Repository r, Class<? extends Hook> hookClazz, String methodName, Object argument) {
        try {
            Hook<?> hook = hookClazz.newInstance();
            hook.setRepository(r);

            Method hookMethod = getMethod(hook, methodName, argument.getClass());

            if (hookMethod == null) {
                hookMethod = getMethod(hook, methodName, Object.class);
            }

            if (hookMethod != null) {
                logHookInvoke(hookClazz, methodName, argument);
                hookMethod.invoke(hook, argument);
            }
        } catch (InstantiationException ex) {
            throw new RuntimeException("The Hook class " + hookClazz.getSimpleName()
                    + " must have a default constructor, and it must not throw exceptions.", ex);
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
            throw ThrownExceptionsUtils.handle(ex);
        }
    }

    private static Method getMethod(Object hook, String methodName, Class<?> argumentClazz) {
        try {
            return hook.getClass().getMethod(methodName, argumentClazz);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static void logHookFound(Object argument, Class<? extends Hook> hookClazz) {
        logger.info(String.format("hook class: %s, endpoint: %s",
                hookClazz.getName(),
                argument.getClass().getName()));
    }

    private static void logHookInvoke(Class<? extends Hook> hookClazz, String methodName, Object argument) {
        logger.info(String.format("hook invoke: %s.%s - endpoint: %s, id: %s",
                hookClazz.getName(),
                methodName,
                argument.getClass().getName(),
                safeGetObjectId(argument)));
    }

    private static String safeGetObjectId(Object argument) {
        if (argument.getClass().equals(IdRef.class)) {
            return ((IdRef<?>) argument).getUri();
        }

        IdRef<?> id = new ObjectHolder(argument).safeGetId();
        if (id != null) {
            return id.getUri();
        }
        return "empty";
    }

}
