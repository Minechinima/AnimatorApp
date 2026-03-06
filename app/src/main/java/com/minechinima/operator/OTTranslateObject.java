package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.IOTModalModifierProvider;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;
import com.minechinima.operator.properties.TranslateProps;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;

public class OTTranslateObject extends OTTransformObject<TranslateObject, TranslateProps> implements IOTModalModifierProvider<BBOperatorContext, TranslateObject, TranslateProps> {
    private final IGenericRegistry<PropertyModifierCommandConfig<TranslateProps>> modifierRegistry;

    public OTTranslateObject(String id, String title, String description, IGenericRegistry<PropertyModifierCommandConfig<TranslateProps>> modifierRegistry) {
        super(id, title, description);
        this.modifierRegistry = modifierRegistry;
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
        return this.modifierRegistry;
    }

    @Override
    public String modifierConfigIdentifier() {
        return "translate_modifiers";
    }
}
