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
}