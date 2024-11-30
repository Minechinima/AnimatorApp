package com.minechinima.ui.elements;

import com.minechinima.model.BBContext;
import com.minechinima.ui.elements.UIWindow;

public class BBUIWindow extends UIWindow {
    public final BBContext context;

    public BBUIWindow(BBContext context) {
        this.context = context;
    }
}
