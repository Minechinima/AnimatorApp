package com.minechinima.ui.elements;

import com.minechinima.model.values.FloatValue;
import com.minechinima.ui.UIContext;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;

public class UIDragFloat extends UIValueElement<Float, FloatValue> {
    protected final String label;
    protected boolean isDragging = false;
    protected boolean isInputtingManually = false;
    protected boolean clicked = false;

    public UIDragFloat(String label) {
        this.label = label;
    }

    public UIDragFloat() {
        this("");
    }

    @Override
    protected void _onClose() {
        this.isDragging = false;
    }

    public UIDragFloat init(FloatValue value) {
        this.value = value;
        return this;
    }

    @Override
    protected void drawElement(UIContext context) {
        float[] newValue = new float[]{this.value.get()};
        boolean changed = ImGui.dragFloat(this.label, newValue);

        if (ImGui.isItemActive()) {
            if (ImGui.isItemClicked(ImGuiMouseButton.Left)) {
                /* clicked this frame on the item */
                this.clicked = true;
            } else if (this.clicked && ImGui.isMouseReleased(ImGuiMouseButton.Left)) {
                /*
                 * item is active and was clicked last frame
                 * the mouse is released so it cant be dragging and must be inputting manually
                 */
                this.isInputtingManually = true;
            }
        }

        if (changed && !this.isInputtingManually && !ImGui.isItemDeactivated()) {
            /* lock threshold of 1 for dragging returns only true if the mouse moved at least 1 pixel since last time */
            if (!this.isDragging && ImGui.isItemActive() && ImGui.isMouseDragging(ImGuiMouseButton.Left, 1)) {
                this.isDragging = true;
            }

            //app.getController().startOrContinueTransaction("",
            //        () -> BaseValueController.setValue(this.value, newValue[0], BaseValueController.UNDO_MERGE));
        }

        if (ImGui.isItemDeactivatedAfterEdit() && changed && this.isInputtingManually) {
            //app.getMainController().executeNewTransactionToHistory("",
            //        () -> BaseValueController.setValue(this.value, newValue[0]));
        }

        if (ImGui.isItemDeactivated()) {
            if (this.isDragging) {
                //app.getController().continueTransaction(
                //        () -> BaseValueController.setValue(this.value, newValue[0], BaseValueController.UNDO_MERGE));
                //app.getController().commitCurrentTransactionToHistory();
            }
            this.isDragging = false;
            this.isInputtingManually = false;
            this.clicked = false;
        }
    }
}
