package com.minechinima.ui.annotation;


public interface IUIHandler<E> {
    // TODO Object type is actually UIContext, but we cant import it here because circular dependency
    void draw(Object context, E element);
    Class<E> getRenderTarget();

    // TODO wenn Accordion --> rekursiv gucken und "addElement" aufrufen
}
