package com.minechinima.ui.elements;

import com.minechinima.ui.UIContext;

public abstract class UISpace extends UIElement {
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
    protected void draw(UIContext context) {
        this.drawMenuBarItems(context);
        this.drawBody(context);
    }
}
