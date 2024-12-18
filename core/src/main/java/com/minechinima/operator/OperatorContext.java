package com.minechinima.operator;

import com.minechinima.ui.UIManager;

/**
 * The context in which an Operator executes.
 */
public class OperatorContext {
    public UIManager uiManager;
    public final UIOperatorContext uiContext;

    public OperatorContext(UIOperatorContext uiContext) {
        this.uiContext = uiContext;
    }
}
