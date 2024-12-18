package com.minechinima.operator;

/**
 * The context in which an Operator executes.
 */
public class OperatorContext {
    private final UIOperatorContext uiContext;

    public OperatorContext(UIOperatorContext uiContext) {
        this.uiContext = uiContext;
    }
}
