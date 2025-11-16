package com.minechinima.operator;

import com.minechinima.ui.UIManager;
import com.minechinima.ui.elements.UIElement;
import com.minechinima.ui.elements.window.UIRegion;
import com.minechinima.ui.elements.window.UIScreen;
import com.minechinima.ui.elements.window.UISpace;
import com.minechinima.ui.elements.window.UIWindow;
import org.jetbrains.annotations.NotNull;

/**
 * This is the context information of the current UI state. It holds all current relevant information
 * how the user interacted with the UI. This information can be used to programme logic for user interaction based on
 * the UI state.
 * @implNote final so a subclass cannot give a UIOperator access to App data.
 * Immutable record to ensure it cannot be changed by other systems other than the UI.
 * This also ensures that it has to be built after the UI rendering cycle is finished.
 * <br><br>
 * The UI elements' generic context type is kept raw as there is no need to spread it here. This enhances flexibility with the
 * context type, as systems that have access to this UI context can't infer the data context type and access it through the UI.<br>
 * <b>The data context should not be accessed through the UI.</b>
 */
public record UIOperatorContext(UIWindow clickedWindow,
                                UIScreen clickedUIScreen,
                                UISpace clickedUISpace,
                                UIRegion clickedUIRegion,
                                UIElement clickedWidget,
                                UIWindow currentHoveredWindow,
                                UIScreen currentHoveredUIScreen,
                                UISpace currentHoveredUISpace,
                                UIRegion currentHoveredUIRegion,
                                UIElement currentHoveredWidget,
                                UIManager manager) implements IContext {

    @Override
    public @NotNull UIOperatorContext getUIContext() {
        return this;
    }

    @Override
    public void setUIContext(UIOperatorContext operatorContext) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
