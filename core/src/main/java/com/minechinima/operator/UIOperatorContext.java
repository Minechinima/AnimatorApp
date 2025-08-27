package com.minechinima.operator;

import com.minechinima.ui.UIManager;
import com.minechinima.ui.elements.UIElement;
import com.minechinima.ui.elements.UIScreen;
import com.minechinima.ui.elements.UISpace;
import com.minechinima.ui.elements.UIWindow;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * This is the context information of the current UI state. It holds all current relevant information
 * how the user interacted with the UI. This information can be used to programme logic for user interaction based on
 * the UI state.
 * @implNote final so a subclass cannot give a UIOperator access to App data.
 * Immutable record to ensure it cannot be changed by other systems other than the UI.
 * This also ensures that it has to be built after the UI rendering cycle is finished.
 */
//TODO make record and make it so that it is created after the UI is finished, this requires an intermediate object to hold
// this data, but it makes it more safe and secure (no need to originalProps the context, also it makes it clear that the UI must first be run through)
public record UIOperatorContext(UIWindow clickedWindow,
                                UIScreen clickedUIScreen,
                                UISpace clickedUISpace,
                                UIElement clickedWidget,
                                UIWindow currentHoveredWindow,
                                UIScreen currentHoveredUIScreen,
                                UISpace currentHoveredUISpace,
                                UIElement currentHoveredWidget,
                                UIManager manager) implements IContext {
    @Override
    public UIOperatorContext getUIContext() {
        return this;
    }

    @Override
    public void setUIContext(UIOperatorContext operatorContext) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UIOperatorContext copy() {
        UIOperatorContext copy = new UIOperatorContext(this.clickedWindow,
                this.clickedUIScreen,
                this.clickedUISpace,
                this.clickedWidget,
                this.currentHoveredWindow,
                this.currentHoveredUIScreen,
                this.currentHoveredUISpace,
                this.currentHoveredWidget,
                this.manager);
        return copy;
    }

    @Override
    public boolean contextEquals(IContext other) {
        return this.equals(other);
    }
}
