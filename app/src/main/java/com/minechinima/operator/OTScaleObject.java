package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.ot.IOTModalModifierProvider;
import com.minechinima.operator.properties.ScaleProps;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;

public class OTScaleObject extends OTTransformObject<ScaleObject, ScaleProps> implements IOTModalModifierProvider<BBOperatorContext, ScaleObject, ScaleProps> {

    public OTScaleObject(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.get() != null;
    }

    @NotNull
    @Override
    public ScaleObject getOperator() {
        return new ScaleObject(new ScaleProps());
    }

    @Override
    public boolean useReleaseEvent() {
        return false;
    }

    @Override
    public ScaleObject getOperator(ScaleProps props) {
        return new ScaleObject(props);
    }

    @Override
    public ScaleProps createProps() {
        return new ScaleProps();
    }

    @Override
    public Class<ScaleProps> getPropertyType() {
        return ScaleProps.class;
    }

    @Override
    public IGenericRegistry<PropertyModifierCommandConfig<ScaleProps>> modifierRegistry() {
        return null;
    }

    @Override
    public String modifierConfigIdentifier() {
        return "transform_modifiers";
    }
}
