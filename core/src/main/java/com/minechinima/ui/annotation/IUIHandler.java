package com.minechinima.ui.annotation;

import com.minechinima.ui.UIContext;

public interface IUIHandler<E> {
    void draw(UIContext context, E element);
    default void init() {
        // default implementation
    }

    // TODO wenn Accordion --> rekursiv gucken und "addElement" aufrufen
}
