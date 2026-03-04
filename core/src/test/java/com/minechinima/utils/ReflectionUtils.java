package com.minechinima.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static Object invokePrivateMethod(
            Object target,
            String methodName,
            Class<?>[] paramTypes,
            Object[] args
    ) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokePrivateMethod(
            Object target,
            String methodName
    ) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);

            return method.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getPrivateField(Object target, String fieldName) {
        try {
            Class<?> clazz = target.getClass();
            java.lang.reflect.Field field = null;

            while (clazz != null) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }

            if (field == null) {
                throw new NoSuchFieldException("Field '" + fieldName + "' not found on " + target.getClass());
            }
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}