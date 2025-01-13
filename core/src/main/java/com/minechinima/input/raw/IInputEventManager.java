package com.minechinima.input.raw;

public interface IInputEventManager {
    <E extends InputEvent, F extends IInputEventListener<E>> void addListener(F listener, Class<E> clazz);
    <E extends InputEvent, F extends IInputEventListener<E>> void removeListener(F listener);
    <E extends InputEvent, F extends IInputEventListener<E>> void removeListener(F listener, Class<E> clazz);
    void notifyListeners(InputEvent event);
}
