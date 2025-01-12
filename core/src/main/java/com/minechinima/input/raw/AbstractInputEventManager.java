package com.minechinima.input.raw;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInputEventManager implements IInputEventManager {
    private final List<IKeyEventListener> listeners = new ArrayList<>();
    private final List<IMouseEventListener> mouseListeners = new ArrayList<>();
    private final List<IScrollEventListener> scrollListeners = new ArrayList<>();

    @Override
    public void addListener(IKeyEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void addListener(IMouseEventListener listener) {
        mouseListeners.add(listener);
    }

    @Override
    public void addListener(IScrollEventListener listener) {
        scrollListeners.add(listener);
    }

    @Override
    public void removeListener(IKeyEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeListener(IMouseEventListener listener) {
        mouseListeners.remove(listener);
    }

    @Override
    public void removeListener(IScrollEventListener listener) {
        scrollListeners.remove(listener);
    }

    @Override
    public void notifyListeners(KeyEvent event) {
        for (IKeyEventListener listener : listeners) {
            listener.onKey(event);
        }
    }

    @Override
    public void notifyListeners(MouseEvent event) {
        for (IMouseEventListener listener : mouseListeners) {
            listener.onMouse(event);
        }
    }

    @Override
    public void notifyListeners(ScrollEvent event) {
        for (IScrollEventListener listener : scrollListeners) {
            listener.onScroll(event);
        }
    }
}
