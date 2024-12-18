package com.minechinima.ui.elements;

import com.minechinima.model.BBContext;
import com.minechinima.operator.UIOperatorContext;
import com.minechinima.ui.BBOperatorContext;
import com.minechinima.ui.elements.UIWindow;

public class BBUIWindow extends UIWindow<BBOperatorContext> {
    public final BBContext context;

    public BBUIWindow(BBOperatorContext context) {
        super(context);
        this.context = context.context;
    }
}
