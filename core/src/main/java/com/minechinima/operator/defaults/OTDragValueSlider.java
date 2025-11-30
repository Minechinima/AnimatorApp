package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.UIOperatorContext;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.UIOperatorTypePropertyProvider;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.ui.elements.UIDragFloat;
import org.jetbrains.annotations.NotNull;

public class OTDragValueSlider extends UIOperatorTypePropertyProvider<UIDragValueSlider, DragValueSliderProp> implements IOTModal<UIDragValueSlider> {
    public OTDragValueSlider(String id, String title, String description) {
        super(id, title, description, true);
    }

    @Override
    public boolean poll(UIOperatorContext context) throws UserException {
        return context.currentHoveredWidget() instanceof UIDragFloat<?>;
    }

    @Override
    public @NotNull UIDragValueSlider getOperator() {
        return new UIDragValueSlider(new DragValueSliderProp());
    }

    @Override
    public boolean confirmOnReleaseDefault() {
        return true;
    }

    @Override
    public UIDragValueSlider getOperator(DragValueSliderProp props) {
        return new UIDragValueSlider(props);
    }

    @Override
    public DragValueSliderProp createProps() {
        return new DragValueSliderProp();
    }
}
