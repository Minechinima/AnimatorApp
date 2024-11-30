package com.minechinima.ui.elements;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.ui.UIContext;

import java.util.function.Consumer;

/**
 * A UIElement that draws elements based on a given BaseStructure. Before drawing the instance of the BaseStructure needs to be set
 * otherwise nothing will be drawn.
 * @param <T>
 */
public abstract class UIValueElement<J, T extends BaseStructure> extends UIElement {
    protected T value;
    protected Consumer<J> setCallback;

    public UIValueElement() {
    }

    public T getValue() {
        return this.value;
    }

    public UIValueElement<J, T> init(T value) {
        this.value = value;
        return this;
    }

    public UIValueElement<J, T> init(T value, Consumer<J> callback) {
        this.value = value;
        this.setCallback = callback;
        return this;
    }

    @Override
    protected final void draw(UIContext context) {
        if (this.value == null) return;
        this.drawElement(context);
        this.value = null;
        this.setCallback = null;
    }

    protected abstract void drawElement(UIContext context);
}
