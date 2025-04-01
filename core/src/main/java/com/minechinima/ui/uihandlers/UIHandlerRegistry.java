package com.minechinima.ui.uihandlers;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.ui.annotation.IUIHandler;
import com.minechinima.ui.annotation.UnnamedUIHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Registry of UIHandlers that can render a data object.
 */
public final class UIHandlerRegistry {
    private final Multimap<Class<? extends IUIHandler<? extends BaseStructure>>, IUIHandler<? extends BaseStructure>> entryPoints = LinkedListMultimap.create();
    private final LinkedList<IUIHandler<? extends BaseStructure>> unnamedUIHandlers = new LinkedList<>();
    private boolean unnamedHandlersInitialized = false;

    public <E extends BaseStructure,F extends IUIHandler<E>> Collection<F> getHandlersFor(Class<F> clazz) {
        if (entryPoints.containsKey(clazz)) {
            return (Collection<F>) entryPoints.get(clazz);
        }

        return registerEntryPoints(clazz);
    }

    public <E extends BaseStructure, F extends IUIHandler<E>> Collection<F> getHandlersFor(Class<F> clazz, E baseStructure) {
        assert baseStructure != null;

        if (entryPoints.containsKey(clazz)) {
            List<F> uiHandlers = new LinkedList<>();
            for (F uiHandler : (Iterable<F>) entryPoints.get(clazz)) {
                if (uiHandler.getRenderTarget() != null && baseStructure.getClass().isAssignableFrom(uiHandler.getRenderTarget())) {
                    uiHandlers.add(uiHandler);
                }
            }

            uiHandlers.sort(Comparator.comparing(o -> getClassDepth(o.getRenderTarget())));

            return uiHandlers;
        }

        List<F> handlers = registerEntryPoints(clazz);
        handlers.sort(Comparator.comparing(o -> getClassDepth(o.getRenderTarget())));
        return handlers;
    }

    public <E extends BaseStructure, F extends IUIHandler<E>> Collection<F> getHandlersForDirectType(Class<F> clazz, E baseStructure) {
        assert baseStructure != null;

        if (entryPoints.containsKey(clazz)) {
            List<F> uiHandlers = new LinkedList<>();
            for (F uiHandler : (Iterable<F>) entryPoints.get(clazz)) {
                if (uiHandler.getRenderTarget() != null && uiHandler.getRenderTarget() == baseStructure.getClass()) {
                    uiHandlers.add(uiHandler);
                }
            }

            return uiHandlers;
        }

        return registerEntryPoints(clazz);
    }

    public <E extends BaseStructure, F extends IUIHandler<E>> Collection<F> getUnnamedHandlersFor(@NotNull E baseStructure) {
        assert baseStructure != null;

        registerUnnamedUIHandlers();

        List<F> uiHandlers = new LinkedList<>();
        for (IUIHandler<? extends BaseStructure> uiHandler : unnamedUIHandlers) {
            if (uiHandler.getRenderTarget() != null && uiHandler.getRenderTarget().isAssignableFrom(baseStructure.getClass())) {
                uiHandlers.add((F) uiHandler);
            }
        }

        uiHandlers.sort(Comparator.comparing(o -> getClassDepth(o.getRenderTarget())));

        return uiHandlers;
    }

    public <E extends BaseStructure, F extends IUIHandler<E>> Collection<F> getUnnamedHandlersForDirectType(@NotNull E baseStructure) {
        assert baseStructure != null;

        registerUnnamedUIHandlers();

        List<F> uiHandlers = new LinkedList<>();
        for (IUIHandler<? extends BaseStructure> uiHandler : unnamedUIHandlers) {
            if (uiHandler.getRenderTarget() != null && uiHandler.getRenderTarget() == baseStructure.getClass()) {
                uiHandlers.add((F) uiHandler);
            }
        }

        return uiHandlers;
    }

    private int getClassDepth(Class<?> clazz) {
        int depth = 0;
        while (clazz != Object.class) {
            clazz = clazz.getSuperclass();
            depth++;
        }
        return depth;
    }

    private void registerUnnamedUIHandlers() {
        if (unnamedHandlersInitialized) {
            return;
        }

        registerEntryPoints(UnnamedUIHandler.class);
    }

    private <E extends BaseStructure,F extends IUIHandler<E>> List<F> registerEntryPoints(Class<F> clazz) {
        ArrayList<F> entryPoints = new ArrayList<>();
        ServiceLoader<F> serviceLoader = ServiceLoader.load(clazz);

        for (F entryPoint : serviceLoader) {
            entryPoints.add(entryPoint);

            if (UnnamedUIHandler.class.isAssignableFrom(clazz)) {
                unnamedHandlersInitialized = true;
                unnamedUIHandlers.add(entryPoint);
            } else {
                registerEntryPoint(clazz, entryPoint);
            }
        }

        return entryPoints;
    }

    private <E extends BaseStructure,F extends IUIHandler<E>> void registerEntryPoint(Class<F> clazz, F entryPoint) {
        entryPoints.put(clazz, entryPoint);
    }
}
