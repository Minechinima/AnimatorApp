package com.minechinima.ui.elements.properties;

import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.UIElement;
import com.minechinima.ui.elements.UITextEditor;

public class UIRenderProperties extends UIElement implements IUIPropertyContext {
    private final UITextEditor text = new UITextEditor("Test text editor");

    @Override
    public String getIconUnicode() {
        return "\uf87c";
    }

    @Override
    public int getIndex() {
        return UIPropertyContextIndex.RENDER;
    }

    @Override
    public void draw(UIContext context) {
        //this.text.init(context.getCurrentWindow().context.database.text).render(context);
    }
}
