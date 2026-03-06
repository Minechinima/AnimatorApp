package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.IOTModalModifierProvider;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;
import com.minechinima.operator.properties.TranslateProps;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class OTRotateObject extends OTTransformObject<RotateObject, RotateProps> implements IOTModalModifierProvider<BBOperatorContext, RotateObject, RotateProps> {
    private final IGenericRegistry<PropertyModifierCommandConfig<RotateProps>> modifierRegistry;

    public OTRotateObject(String id, String title, String description, IGenericRegistry<PropertyModifierCommandConfig<RotateProps>> modifierRegistry) {
        super(id, title, description);
        this.modifierRegistry = modifierRegistry;
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
        return modifierRegistry;
    }

    @Override
    public String modifierConfigIdentifier() {
        return "rotate";
    }
}
