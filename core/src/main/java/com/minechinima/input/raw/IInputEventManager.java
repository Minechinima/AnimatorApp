package com.minechinima.input.raw;

public interface IInputEventManager {
    void addListener(IInputEventListener listener);
    void removeListener(IInputEventListener listener);
    void notifyListeners(InputEvent event);
}
