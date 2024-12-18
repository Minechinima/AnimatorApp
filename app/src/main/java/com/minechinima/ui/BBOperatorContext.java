package com.minechinima.ui;

import com.minechinima.model.BBContext;
import com.minechinima.operator.OperatorContext;
import com.minechinima.operator.UIOperatorContext;

public class BBOperatorContext extends OperatorContext {
    public final BBContext context;
    public BBOperatorContext(UIOperatorContext uiContext, BBContext context) {
        super(uiContext);
        this.context = context;
    }
}
