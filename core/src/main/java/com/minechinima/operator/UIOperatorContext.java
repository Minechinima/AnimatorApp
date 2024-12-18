package com.minechinima.operator;

import com.minechinima.ui.elements.UIElement;
import com.minechinima.ui.elements.UIScreen;
import com.minechinima.ui.elements.UISpace;
import com.minechinima.ui.elements.UIWindow;

import javax.annotation.Nullable;

/**
 * @implNote final so a subclass cannot give a UIOperator access to App data.
 */
public final class UIOperatorContext {
    /*
     * The UI context in which the operator was triggered in.
     * This might diverge from the current UI context
     * when the mouse moves to another context
     */
    public UIWindow clickedWindow;
    public UIScreen clickedUIScreen;
    public UISpace clickedUISpace;
    //public UIWindow clickedUIRegion;
    @Nullable
    public UIElement clickedWidget;

    public UIWindow currentWindow;
    public UIScreen currentUIScreen;
    public UISpace currentUISpace;
    //public UIWindow currentUIRegion;
    @Nullable
    public UIElement currentWidget;
    //TODO this needs to be UI data interface
}
