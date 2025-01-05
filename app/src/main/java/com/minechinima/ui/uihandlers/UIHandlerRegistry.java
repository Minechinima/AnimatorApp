package com.minechinima.ui.uihandlers;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.minechinima.ui.annotation.IUIHandler;

import java.util.ArrayList;
import java.util.ServiceLoader;

public final class UIHandlerRegistry {
    private final Multimap<Class<? extends IUIHandler<?>>, IUIHandler<?>> entryPoints = LinkedListMultimap.create();

    public <E,F extends IUIHandler<E>> Iterable<F> getEntryPoints(Class<F> clazz) {
        if (entryPoints.containsKey(clazz)) {
            return (Iterable<F>) entryPoints.get(clazz);
        }

        return registerEntryPoints(clazz);
    }

    private <E,F extends IUIHandler<E>> Iterable<F> registerEntryPoints(Class<F> clazz) {
        ArrayList<F> entryPoints = new ArrayList<>();
        ServiceLoader<F> serviceLoader = ServiceLoader.load(clazz);

        for (F entryPoint : serviceLoader) {
            entryPoints.add(entryPoint);
            registerEntryPoint(clazz, entryPoint);
        }

        return entryPoints;
    }

    private <E,F extends IUIHandler<E>> void registerEntryPoint(Class<F> clazz, F entryPoint) {
        entryPoints.put(clazz, entryPoint);
    }
}
