package com.minechinima.ui.elements.properties;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.ui.IchBinEinsParentUIHandler;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.annotation.IUIHandler;
import com.minechinima.ui.elements.UIElement;

public abstract class UIPropertyContext<T extends BaseStructure> extends UIElement implements IUIPropertyContext {
    private T context;
    private final Class<T> type;

    protected UIPropertyContext(Class<T> type) {
        this.type = type;
    }

    public T getContext() {
        return this.context;
    }

    public Class<T> getContextType() {
        return this.type;
    }

    public UIPropertyContext<T> setContext(T context) {
        this.context = context;
        return this;
    }

    /**
     * @param context
     * @return true when this property context panel should show on the given context object.
     */
    public boolean shouldShow(T context) {
        return true;
    }

    @Override
    public final void draw(UIContext context) {
        if (this.context == null) return;
        this.drawBody(context);
        this.context = null;
    }

    protected abstract void drawBody(UIContext context);

    protected final void drawFromEntryPoint(Class<? extends IUIHandler<T>> entryPointClass, UIContext context) {
        for (IUIHandler<T> entryPoint : context.getUiHandlerRegistry().getHandlersFor(entryPointClass)) {
            entryPoint.draw(context, this.context);
        }
    }

    protected final void drawFromObject(UIContext context) {
        Iterable<IchBinEinsParentUIHandler> handlers = context.getUiHandlerRegistry().getHandlersFor(IchBinEinsParentUIHandler.class, this.context);
        for (IchBinEinsParentUIHandler entryPoint : handlers) {
            entryPoint.draw(context, this.context);
        }
    }
}
