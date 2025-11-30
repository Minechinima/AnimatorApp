package com.minechinima.operator.config.modal;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.config.IReleasableModule;
import com.minechinima.operator.modal.IPropertyModifierReleasableCommand;

/**
 * Configuraton for a property modifier command that can react to release event.
 * @param <P>
 */
public abstract class PropertyModifierReleaseableConfig<P extends OperatorPropertiesGroupStruct> extends PropertyModifierConfig<P> implements IReleasableModule {

    public PropertyModifierReleaseableConfig(String title, String description) {
        super(title, description);
    }

    @Override
    public abstract IPropertyModifierReleasableCommand<P, ?> factory();
}
