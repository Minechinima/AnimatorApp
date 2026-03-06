package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.ot.IOTModalModifierProvider;
import com.minechinima.operator.properties.TranslateProps;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;

public class OTTranslateObject extends OTTransformObject<TranslateObject, TranslateProps> implements IOTModalModifierProvider<BBOperatorContext, TranslateObject, TranslateProps> {

    public OTTranslateObject(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.get() != null;
    }

    @Override
    public @NotNull TranslateObject getOperator() {
        return new TranslateObject(new TranslateProps());
    }

    @Override
    public boolean useReleaseEvent() {
        return false;
    }

    @Override
    public TranslateObject getOperator(TranslateProps props) {
        return new TranslateObject(props);
    }

    @Override
    public TranslateProps createProps() {
        return new TranslateProps();
    }

    @Override
    public Class<TranslateProps> getPropertyType() {
        return TranslateProps.class;
    }

    @Override
    public IGenericRegistry<PropertyModifierCommandConfig<TranslateProps>> modifierRegistry() {
        return null;
    }

    @Override
    public String modifierConfigIdentifier() {
        return "transform_modifiers";
    }
}
