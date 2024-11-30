package com.minechinima.ui.elements;

import com.minechinima.ui.UIContext;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public abstract class UIElement {
    private boolean shown;
    /**
     * Mainly used for eventlisteners and not for rendering.
     */
    protected final List<UIElement> children = new ArrayList<>();

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
    public void onShow() {
        this._onShow();
        this.children.forEach(UIElement::onShow);
    }

    /**
     * This method is supposed to be overridden to define logic to happen when the UI is shown.
     */
    protected void _onShow() { }

    public void render(UIContext context) {
        if (!context.getImguiContext().isBeginFrame()) return;
        if (!this.shown) this.onShow();
        this.shown = true;
        ImGui.pushID(this.hashCode());
        this.draw(context);
        ImGui.popID();
    }

    protected abstract void draw(UIContext context);

    public void triggerEvent() {

    }
}
