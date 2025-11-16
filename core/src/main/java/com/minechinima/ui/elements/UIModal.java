package com.minechinima.ui.elements;

import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.window.UIWindow;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

import java.util.List;
import java.util.function.Consumer;

/**
 * A UI popup that blocks the user interaction with the underlying UI.
 * A Modal is used for a user action that must be explicitly confirmed or dismissed.
 */
public class UIModal<C extends IContext> extends UIElement<C> {
    private boolean drawn;
    /**
     * Whether to cancel this UI Modal when the user clicks outside the popup window.
     */
    protected final boolean cancelOnClickOutside;
    private final Consumer<Boolean> onClose;
    public UIWindow<C> owner;

    private final UIButton cancel = new UIButton("Cancel", button -> this.cancelModal());
    private final UIButton confirm = new UIButton("Confirm", button -> this.confirmModal());

    public UIModal(UIElement<C>... content) {
        this(null, false, content);
    }

    public UIModal(boolean cancelOnClickOutside, UIElement<C>... content) {
        this(null, cancelOnClickOutside, content);
    }

    public UIModal(Consumer<Boolean> onClose, UIElement<C>... content) {
        this(onClose, false, content);
    }

    public UIModal(Consumer<Boolean> onClose, boolean cancelOnClickOutside, UIElement<C>... content) {
        this.children.addAll(List.of(content));
        this.cancelOnClickOutside = cancelOnClickOutside;
        this.onClose = onClose;
    }

    public void confirmModal() {
        if (this.onClose != null) this.onClose.accept(true);
        this.owner.closeModal();
    }

    public void cancelModal() {
        if (this.onClose != null) this.onClose.accept(false);
        this.owner.closeModal();
    }

    @Override
    public void draw(UIContext<C> context) {
        if (!this.drawn) {
            ImGui.openPopup("MyModalPopup");
            this.drawn = true;
        }
        if (ImGui.beginPopupModal("MyModalPopup", ImGuiWindowFlags.AlwaysAutoResize)) {
            for (UIElement<C> child : this.children) {
                child.render(context);
            }

            this.cancel.render(context);
            this.confirm.render(context);

            ImGui.endPopup();
        }
    }
}
