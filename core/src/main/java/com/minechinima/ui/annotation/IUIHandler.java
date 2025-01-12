package com.minechinima.ui.annotation;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.ui.UIContext;

public interface IUIHandler<E extends BaseStructure> {
    void draw(UIContext context, E element);
    Class<E> getRenderTarget();

    // TODO wenn Accordion --> rekursiv gucken und "addElement" aufrufen
}
