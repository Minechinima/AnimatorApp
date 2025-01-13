package com.minechinima.input.raw;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public abstract class AbstractInputEventManager implements IInputEventManager {
    private final Multimap<Class<? extends InputEvent>, IInputEventListener<InputEvent>> listeners = LinkedListMultimap.create();

    @Override
    public <E extends InputEvent, F extends IInputEventListener<E>> void addListener(F listener, Class<E> clazz) {
        IInputEventListener<InputEvent> theListener = (IInputEventListener<InputEvent>) listener;
        listeners.put(clazz, theListener);
    }

    @Override
    public <E extends InputEvent, F extends IInputEventListener<E>> void removeListener(F listener) {
        listeners.values().remove(listener);
    }

    @Override
    public <E extends InputEvent, F extends IInputEventListener<E>> void removeListener(F listener, Class<E> clazz) {
        listeners.get(clazz).remove(listener);
    }

    @Override
    public void notifyListeners(InputEvent event) {
        for (IInputEventListener<InputEvent> listener : listeners.get(event.getClass())) {
            listener.onEvent(event);
        }
        for (IInputEventListener<InputEvent> listener : listeners.get(InputEvent.class)) {
            listener.onEvent(event);
        }
    }
}
