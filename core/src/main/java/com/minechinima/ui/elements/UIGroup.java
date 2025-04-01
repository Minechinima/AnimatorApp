package com.minechinima.ui.elements;

import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.annotation.IUIElement;
import com.minechinima.ui.annotation.IUIGroup;

public abstract class UIGroup<C extends IContext> extends UIElement<C> implements IUIGroup<UIContext<C>> {
    public abstract void addChild(IUIElement<UIContext<C>> element);
}
