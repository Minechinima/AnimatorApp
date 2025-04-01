package com.minechinima.ui.elements;

import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;

public abstract class UISpace<C extends IContext> extends UIElement<C> {
    private final String panelName;

    public UISpace(String panelName) {
        this.panelName = panelName;
    }

    public String getPanelName() {
        return this.panelName;
    }

    /**
     *
     * @return the aspect ratio this panel should be when first appearing
     *         Returns a decimal in the aspect ratio format width/height
     */
    public float getDefaultAspectRatio() {
        return 16F/9F;
    }

    public abstract void drawMenuBarItems(UIContext context);

    public abstract void drawBody(UIContext context);

    @Override
    public void draw(UIContext context) {
        this.drawMenuBarItems(context);
        this.drawBody(context);
    }
}
