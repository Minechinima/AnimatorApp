package com.minechinima.input.raw;

@FunctionalInterface
public interface IInputEventListener<T extends InputEvent> {
    void onEvent(T event);
}
