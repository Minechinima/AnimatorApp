package com.minechinima.ui.elements;

import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;

public class UIDragFloat<C extends IContext> extends UIValueElement<Float, FloatValue, C> {
    protected final String label;
    protected boolean isDragging = false;
    protected boolean isInputtingManually = false;
    protected boolean clicked = false;
    public boolean highlight = false;

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

    public UIDragFloat<C> init(FloatValue value) {
        this.value = value;
        return this;
    }

    @Override
    protected void drawElement(UIContext<C> context) {
        float[] newValue = new float[]{this.value.get()};

        if (this.clicked) {
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0xFFAA33FF);
        }

        if (this.highlight) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, new ImVec4(0.2f, 0.6f, 1.0f, 1.0f));
            ImGui.pushStyleColor(ImGuiCol.FrameBgActive, new ImVec4(0.2f, 0.6f, 1.0f, 1.0f));
            ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, new ImVec4(0.2f, 0.6f, 1.0f, 1.0f));
        }

        boolean changed = ImGui.dragFloat(this.label, newValue);

        if (this.clicked) {
            ImGui.popStyleColor();
        }

        if (this.highlight) {
            ImGui.popStyleColor();
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }

        if (ImGui.isMouseHoveringRect(ImGui.getItemRectMinX(), ImGui.getItemRectMinY(), ImGui.getItemRectMaxX(), ImGui.getItemRectMaxY())) {
            context.getContextBuilder().setCurrentHoveredWidget(this);
        }

        if (ImGui.isItemActive()) {
            if (ImGui.isItemClicked(ImGuiMouseButton.Left)) {
                /* clicked this frame on the item */
                this.clicked = true;
            }
        }

        if (this.clicked && ImGui.isItemActive() && ImGui.isMouseReleased(ImGuiMouseButton.Left)) {
            /*
             * item is active and was clicked last frame
             * the mouse is released so it cant be dragging and must be inputting manually
             */
            this.isInputtingManually = true;
            this.clicked = false;
        }

        if (this.clicked) {
            context.getContextBuilder().setClickedWidget(this);
        }



        if (ImGui.isMouseDragging(ImGuiMouseButton.Left, 0) && !this.isInputtingManually) {
            /* lock threshold of 1 for dragging returns only true if the mouse moved at least 1 pixel since last time */
            if (ImGui.isItemActive()) {
                imgui.internal.ImGui.clearActiveID();
            }

            //app.getController().startOrContinueTransaction("",
            //        () -> BaseValueController.setValue(this.value, newValue[0], BaseValueController.UNDO_MERGE));
        }

        if (ImGui.isItemDeactivatedAfterEdit() && changed && this.isInputtingManually) {
            //app.getMainController().executeNewTransactionToHistory("",
            //        () -> BaseValueController.setValue(this.value, newValue[0]));
        }

        if (ImGui.isItemDeactivated()) {
            this.clicked = false;
            this.isDragging = false;
            this.isInputtingManually = false;

        }
    }
}
