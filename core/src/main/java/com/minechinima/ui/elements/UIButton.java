package com.minechinima.ui.elements;

import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.ui.UIContext;
import imgui.ImGui;

import java.util.function.Consumer;

/**
 * A UI Button that performs an action when clicked by the user.
 */
public class UIButton<C extends IContext> extends UIElement<C> {
    private final Consumer<UIButton<C>> onClick;
    private final OperatorType<C> operatorType;
    private final String key;

    public UIButton(String key, Consumer<UIButton<C>> onClick) {
        this.onClick = onClick;
        this.operatorType = null;
        this.key = key;
    }

    public UIButton(String key, OperatorType<C> operatorType) {
        this.onClick = null;
        this.operatorType = operatorType;
        this.key = key;
    }

    public UIButton(String key, Consumer<UIButton<C>> onClick, OperatorType<C> operatorType) {
        this.onClick = onClick;
        this.operatorType = operatorType;
        this.key = key;
    }


    @Override
    public void draw(UIContext<C> context) {
        if (ImGui.button(this.key)) {
            if (this.operatorType != null) {
                context.getUIManager().getOperatorManager().invoke(this.operatorType);
            }

            if (this.onClick != null) {
                this.onClick.accept(this);
            }
        }
    }
}
