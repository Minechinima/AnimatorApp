package com.minechinima.ui.elements.properties;

import java.util.*;

/**
 * This class registers UIPropertyContext instances. These instances should be reusable and not store any UI dependent states
 * as they could be rendered multiple times in different UIs.
 */
public class PropertyContextRegistry {
    /**
     * Register PropertyContexts that are dependent on a specific data e.g. when the user selects a different object.
     */
    private final Set<UIPropertyContext<?>> registry = Collections.newSetFromMap(new IdentityHashMap<>());
    /**
     * Register PropertyContexts that can edit any data and are independent of current user selections.
     */
    private final Set<IUIPropertyContext> standardBodies = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Registers the given property context instance if it does not exist yet
     * @param ctx
     * @return true when the provided context was successfully registered.
     */
    public boolean registerUIPropertyContext(UIPropertyContext<?> ctx) {
        return this.registry.add(ctx);
    }

    /**
     *
     * @param type
     * @return a list of UIPropertyContexts where the context type is either a superclass or the same class as the provided class.
     * @param <T>
     */
    public <T> List<UIPropertyContext<? super T>> getPropertyContexts(Class<T> type) {
        List<UIPropertyContext<? super T>> collected = new ArrayList<>();
        for (UIPropertyContext<?> registered : this.registry) {
            if (registered.getContextType().isAssignableFrom(type)) {
                collected.add((UIPropertyContext<? super T>) registered);
            }
        }

        return collected;
    }

    public <T> Set<UIPropertyContext<? super T>> getReadyToShowContexts(T context) {
        Set<UIPropertyContext<? super T>> collected = Collections.newSetFromMap(new IdentityHashMap<>());

        if (context == null) return collected;

        for (UIPropertyContext registered : this.registry) {
            if (registered.getContextType().isAssignableFrom(context.getClass()) && registered.shouldShow(context)) {
                collected.add((UIPropertyContext<? super T>) registered.setContext(context));
            }
        }

        return collected;
    }

    /**
     * Registers the given property context instance if it does not exist yet
     * @param ctx
     * @return true when the provided context was successfully registered.
     */
    public boolean registerStandardUIPropertyContext(IUIPropertyContext ctx) {
        return this.standardBodies.add(ctx);
    }

    public Set<IUIPropertyContext> getStandardBodies() {
        Set<IUIPropertyContext> copy = Collections.newSetFromMap(new IdentityHashMap<>());
        copy.addAll(this.standardBodies);
        return copy;
    }
}
