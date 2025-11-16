package com.minechinima.operator;

import org.jetbrains.annotations.NotNull;

/**
 * The super interface for a generic context type.
 * Every context for operators always returns a UI context.
 */
public interface IContext {
    @NotNull
    UIOperatorContext getUIContext();

    /**
     * To update the finished UIOperatorContext after the UI has fully built it.
     * @param operatorContext
     */
    void setUIContext(UIOperatorContext operatorContext);
}
