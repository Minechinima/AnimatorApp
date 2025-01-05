package com.minechinima.input.raw;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInputEventManager implements IInputEventManager {
    private final List<IInputEventListener> listeners = new ArrayList<>();

    @Override
    public void addListener(IInputEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IInputEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListeners(InputEvent event) {
        for (IInputEventListener listener : listeners) {
            listener.onInputEvent(event);
        }
    }
}
