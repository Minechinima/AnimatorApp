package com.minechinima.ui.elements;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.annotation.IUIHandler;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Dummy element to render a UI Element
 */
public class UIHandlerDummy<T extends BaseStructure, C extends IContext> extends UIElement<C> {
    private T data;
    private final List<IUIHandler<T>> handlers = new LinkedList<>();

    public UIHandlerDummy(T data, IUIHandler<T>... handlers) {
        this.handlers.addAll(List.of(handlers));
        this.data = data;
    }

    public UIHandlerDummy(T data, Collection<IUIHandler<T>> handlers) {
        this.handlers.addAll(handlers);
        this.data = data;
    }

    @Override
    public void draw(UIContext<C> context) {
        if (this.data == null) return;
        for (IUIHandler<T> handler : this.handlers) {
            handler.draw(context, this.data);
        }
    }
}
