package com.minechinima.ui.elements.window;

import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.UIElement;
import com.minechinima.ui.elements.UIModal;
import com.minechinima.utils.Pair;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

/**
 * OS Window. One window can have multiple (detachable) screens (sub-windows)
 * @param <C>
 */
public class UIWindow<C extends IContext> extends UIElement<C> {
    public final List<UIScreen<C>> screens = new ArrayList<>();
    //TODO discuss where to put modal
    private Pair<UIModal, Vector2i> modalPopup;
    private final long glWindowPtr = 0; //TODO

    public UIWindow() {
    }

    public boolean canOpenModal() {
        return this.modalPopup == null;
    }

    /**
     * Opens a modal at the given position. The Modal is centered at the position.
     * Only adds a modal if {@link #canOpenModal()} is true. Otherwise does nothing.
     * @param modal
     * @return true if a modal could be opened
     */
    public void openModalAt(UIModal modal, int x, int y) {
        if (!this.canOpenModal()) return;
        this.modalPopup = new Pair<>(modal, new Vector2i(x, y));
        modal.owner = this;
    }

    /**
     * Opens a modal centered in the window.<br>
     * Only opens a modal if {@link #canOpenModal()} is true. Otherwise does nothing.
     * @param modal
     */
    public void openModalCentered(UIModal modal) {
        if (!this.canOpenModal()) return;
    }

    /**
     * Closes the current modal, if one was opened.
     */
    public void closeModal() {
        if (this.modalPopup == null) return;
        UIModal modal = this.modalPopup.getA();
        this.modalPopup = null;
        modal.onClose();
    }

    @Override
    public void draw(UIContext<C> context) {
        if (ImGui.beginMainMenuBar()) {
            ImGui.text("BB DEV");
            ImGui.menuItem("Edit");

            if (ImGui.beginMenu("Window")) {
                if (ImGui.beginMenu("New Window")) {
                    for (String panelName : context.getPanelFactory().getRegisteredNames()) {
                        if (ImGui.menuItem(panelName)) {
                            this.screens.add(new UIScreen<>(context.getPanelFactory().createUIPanel(panelName)));
                        }
                    }
                    ImGui.endMenu();
                }
                ImGui.endMenu();
            }

            ImGui.menuItem("Help");
        }
        ImGui.endMainMenuBar();

        for (UIScreen<C> screen : this.screens) {
            screen.draw(context);
        }

        if (this.modalPopup != null) {
            ImGui.setNextWindowPos(this.modalPopup.getB().x, this.modalPopup.getB().y, ImGuiCond.Appearing);
            this.modalPopup.getA().render(context);
        }
    }
}
