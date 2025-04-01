package com.minechinima.ui.elements;

import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.annotation.IUIElement;
import imgui.ImGui;

import java.util.LinkedHashSet;

public abstract class UIElement<C extends IContext> implements IUIElement<UIContext<C>> {
    private boolean shown;
    /**
     * Mainly used for eventlisteners and not for rendering.
     */
    protected final LinkedHashSet<UIElement<C>> children = new LinkedHashSet<>();

    public boolean isShown() {
        return this.shown;
    }

    /**
     * This gets called when the UIElement is closed.
     */
    public final void onClose() {
        this.shown = false;
        this._onClose();
        this.children.forEach(UIElement::onClose);
    }

    /**
     * This method is supposed to be overridden to define logic to happen when the UI is closed.
     */
    protected void _onClose() { }

    /**
     * This gets called when the UIElement is rendered for the first time
     * or is rendered again after being closed.
     */
    public void onShow(UIContext<C> context) {
        this._onShow(context);
        this.children.forEach(e -> e.onShow(context));
    }

    /**
     * This method is supposed to be overridden to define logic to happen when the UI is shown.
     */
    protected void _onShow(UIContext<C> context) { }

    public void render(UIContext<C> context) {
        if (!context.getImguiContext().isBeginFrame()) return;
        if (!this.shown) this.onShow(context);
        this.shown = true;
        ImGui.pushID(this.hashCode());
        this.draw(context);
        ImGui.popID();
    }

    public abstract void draw(UIContext<C> context);

    public void triggerEvent() {

    }
}
