package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.UIOperatorContext;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.ot.IOTModalModifierProvider;
import com.minechinima.operator.ot.UIOperatorTypePropertyProvider;
import com.minechinima.ui.elements.UIDragFloat;
import com.minechinima.utils.GenericRegistry;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;

public class OTDragValueSlider extends UIOperatorTypePropertyProvider<UIDragValueSlider, DragValueSliderProp>
        implements IOTModalModifierProvider<UIOperatorContext, UIDragValueSlider, DragValueSliderProp> {
    public OTDragValueSlider(String id, String title, String description) {
        super(id, title, description, true);
    }
    private final IGenericRegistry<PropertyModifierCommandConfig<DragValueSliderProp>> modifierRegistry = new GenericRegistry<>();
    @Override
    public boolean poll(UIOperatorContext context) throws UserException {
        return context.currentHoveredWidget() instanceof UIDragFloat<?>;
    }

    @Override
    public @NotNull UIDragValueSlider getOperator() {
        return new UIDragValueSlider(new DragValueSliderProp());
    }

    @Override
    public boolean useReleaseEvent() {
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

    @Override
    public Class<DragValueSliderProp> getPropertyType() {
        return DragValueSliderProp.class;
    }

    @Override
    public IGenericRegistry<PropertyModifierCommandConfig<DragValueSliderProp>> modifierRegistry() {
        return this.modifierRegistry;
    }

    @Override
    public String modifierConfigIdentifier() {
        return "drag_ui_slider";
    }
}
