package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;
import com.minechinima.ui.panels.UIViewport;

public class OTZoomViewport extends OperatorTypePropertyProvider<ZoomViewportProps, BBOperatorContext> {
    public OTZoomViewport(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public ZoomViewportProps createProps() {
        return new ZoomViewportProps("");
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.getUIContext().clickedUISpace() instanceof UIViewport;
    }
}
