package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.ui.panels.UIViewport;
import org.jetbrains.annotations.NotNull;

public class OTPanViewport extends OperatorType<BBOperatorContext, PanViewportOP> implements IOTModal<BBOperatorContext, PanViewportOP> {
    public OTPanViewport(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.getUIContext().clickedUISpace() instanceof UIViewport;
    }

    @NotNull
    @Override
    public PanViewportOP getOperator() {
        return new PanViewportOP();
    }

    @Override
    public boolean useReleaseEvent() {
        return true;
    }
}
