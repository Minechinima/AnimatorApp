package com.minechinima.input.raw;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public abstract class AbstractInputEventManager implements IInputEventManager {
    private final Multimap<Class<? extends InputEvent>, IInputEventListener<InputEvent>> listeners = LinkedListMultimap.create();

    @Override
    public <E extends InputEvent, F extends IInputEventListener<E>> void addListener(F listener, Class<E> clazz) {
        IInputEventListener<InputEvent> theListener = (IInputEventListener<InputEvent>) listener;
        this.listeners.put(clazz, theListener);
    }

    @Override
    public <E extends InputEvent, F extends IInputEventListener<E>> void removeListener(F listener) {
        this.listeners.values().remove(listener);
    }

    @Override
    public <E extends InputEvent, F extends IInputEventListener<E>> void removeListener(F listener, Class<E> clazz) {
        this.listeners.get(clazz).remove(listener);
    }

    @Override
    public void notifyListeners(InputEvent event) {
        //TODO proper inheritance of events, do this together with eventbus refactor so they can use the same code and annotation system....
        for (IInputEventListener<InputEvent> listener : this.listeners.get(event.getClass())) {
            listener.onEvent(event);
        }
        for (IInputEventListener<InputEvent> listener : this.listeners.get(InputEvent.class)) {
            listener.onEvent(event);
        }
    }
}
