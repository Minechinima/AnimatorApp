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

    /**
     * @return a new {@link IContext} instance.<br>
     * Whether it is a deep copy is up to the implementation.
     */
    @NotNull
    IContext copy();

    /**
     * This tests whether this and the other context represent the same application context.<br><br>
     * This should not check for reference equality of the {@link IContext} instances, as those might be different, but
     * the actual data (e.g. {@link UIOperatorContext} or other app relevant context data) are equal references!<br><br>
     * This method is used to determine if the application context changed.
     * @param other
     * @return true if the contexts are the same.<br>false if the contexts are not equal and should be treated as a changed context.
     * TODO review
     */
    boolean contextEquals(IContext other);
}
