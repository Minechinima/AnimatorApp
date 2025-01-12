package com.minechinima.input.raw;

public interface IInputEventManager {
    void addListener(IKeyEventListener listener);
    void addListener(IMouseEventListener listener);
    void addListener(IScrollEventListener listener);
    void removeListener(IKeyEventListener listener);
    void removeListener(IMouseEventListener listener);
    void removeListener(IScrollEventListener listener);
    void notifyListeners(KeyEvent event);
    void notifyListeners(MouseEvent event);
    void notifyListeners(ScrollEvent event);
}
