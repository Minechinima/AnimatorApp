package com.minechinima.ui.elements;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.ui.UIContext;
import imgui.ImGui;
import imgui.flag.ImGuiComboFlags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class UIPointerTargetSelector<T extends BaseStructure> extends UIValueElement<T, ValuePointer<T>> {
    private BaseValueHierarchy<T> sourceList = null;
    private final Predicate<T> filter;
    private final String defaultLabel;

    public UIPointerTargetSelector(@NotNull String defaultLabel, @Nullable Predicate<T> filter) {
        this.filter = filter;
        this.defaultLabel = defaultLabel;
    }

    public UIPointerTargetSelector(@NotNull String defaultLabel) {
        this(defaultLabel, null);
    }

    public UIPointerTargetSelector(@Nullable Predicate<T> filter) {
        this("", filter);
    }

    public void setSourceList(BaseValueHierarchy<T> source) {
        this.sourceList = source;
    }

    @Override
    protected void drawElement(UIContext context) {
        try {
            T currentTarget = this.value.getReference();
            String preview = currentTarget != null ? currentTarget.getIdentifier() : this.defaultLabel;
            if (ImGui.beginCombo("", preview, ImGuiComboFlags.NoArrowButton)) {
                if (this.sourceList != null) {
                    List<T> elements = new ArrayList<>(this.sourceList.getChildren());
                    if (this.filter != null) {
                        elements = this.sourceList.getChildren().stream().filter(this.filter).toList();
                    }
                    for (T child : elements) {
                        //TODO what about children that are skipped with empty paths - method in basevaluehierarchy
                        boolean isSelected = child == currentTarget;
                        if (ImGui.selectable(child.getIdentifier(), isSelected)) {
                            if (this.setCallback != null) this.setCallback.accept(child);
                        }
                        if (isSelected) ImGui.setItemDefaultFocus();
                    }
                } else {
                    ImGui.text("No source defined to choose pointer targets from...");
                }

                ImGui.endCombo();
            }
        } finally {
            this.sourceList = null;
        }
    }
}
