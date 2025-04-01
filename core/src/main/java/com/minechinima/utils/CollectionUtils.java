package com.minechinima.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CollectionUtils {
    public static <T> Map<Integer, T> toMap(Iterable<T> iterable) {
        Map<Integer, T> map = new HashMap<>();
        int i = 0;
        for (Iterator<T> it = iterable.iterator(); it.hasNext(); i++) {
            T v = it.next();
            map.put(i, v);
        }
        return map;
    }

    public static <T> Map<Integer, T> toMap(T[] array) {
        Map<Integer, T> map = new HashMap<>();
        for (int i = 0; i < array.length; i++) map.put(i, array[i]);
        return map;
    }
}
