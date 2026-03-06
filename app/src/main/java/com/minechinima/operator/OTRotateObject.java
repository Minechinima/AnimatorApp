package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.ot.IOTModalModifierProvider;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;

public class OTRotateObject extends OTTransformObject<RotateObject, RotateProps> implements IOTModalModifierProvider<BBOperatorContext, RotateObject, RotateProps> {

    public OTRotateObject(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean useReleaseEvent() {
        return false;
    }

    @Override
    public RotateObject getOperator(RotateProps props) {
        return new RotateObject(props);
    }

    @Override
    public RotateProps createProps() {
        return new RotateProps();
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.get() != null;
    }

    @NotNull
    @Override
    public RotateObject getOperator() {
        return new RotateObject(new RotateProps());
    }

    @Override
    public Class<RotateProps> getPropertyType() {
        return RotateProps.class;
    }

    @Override
    public IGenericRegistry<PropertyModifierCommandConfig<RotateProps>> modifierRegistry() {
        return null;
    }

    @Override
    public String modifierConfigIdentifier() {
        return "rotate_modifiers";
    }
}
