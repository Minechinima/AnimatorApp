package com.minechinima.operator;

import com.minechinima.model.BBContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BBOperatorContext implements IContext {
    private UIOperatorContext uiOperatorContext;
    private final BBContext bbContext;

    public BBOperatorContext(BBContext bbContext, UIOperatorContext uiOperatorContext) {
        this.uiOperatorContext = uiOperatorContext;
        this.bbContext = bbContext;
    }

    @NotNull
    public BBContext bbContext() {
        return this.bbContext;
    }

    @Override
    public UIOperatorContext getUIContext() {
        return this.uiOperatorContext;
    }

    @Override
    public void setUIContext(UIOperatorContext operatorContext) {
        this.uiOperatorContext = operatorContext;
    }
}
