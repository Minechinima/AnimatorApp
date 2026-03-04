package com.minechinima.ui.elements;

import com.minechinima.model.api.IData;
import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;

import java.util.function.Consumer;

/**
 * A UIElement that draws elements based on a given BaseStructure. Before drawing the instance of the BaseStructure needs to be set
 * otherwise nothing will be drawn.
 * @param <T>
 */
public abstract class UIValueElement<J, T extends IData, C extends IContext> extends UIElement<C> {
    protected T value;
    protected Consumer<J> setCallback;

    public UIValueElement() {
    }

    public T getValue() {
        return this.value;
    }

    public UIValueElement<J, T, C> init(T value) {
        this.value = value;
        return this;
    }

    public UIValueElement<J, T, C> init(T value, Consumer<J> callback) {
        this.value = value;
        this.setCallback = callback;
        return this;
    }

    @Override
    public final void draw(UIContext<C> context) {
        if (this.value == null) return;
        this.drawElement(context);
        //this.value = null;
        this.setCallback = null;
    }

    protected abstract void drawElement(UIContext<C> context);
}
