package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.UIOperatorContext;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.UIOperatorTypePropertyProvider;
import com.minechinima.ui.elements.UIDragFloat;

public class OTDragValueSlider extends UIOperatorTypePropertyProvider<DragValueSliderProp> implements IOTPropertyProvider<DragValueSliderProp> {
    public OTDragValueSlider(String id, String title, String description, int flags) {
        super(id, title, description, flags);
    }

    public OTDragValueSlider(String id, String title, String description) {
        super(id, title, description, UNDO);
    }

    @Override
    public boolean poll(UIOperatorContext context) throws UserException {
        return context.currentHoveredWidget() instanceof UIDragFloat;
    }

    @Override
    public DragValueSliderProp createProps() {
        return new DragValueSliderProp("");
    }
}
