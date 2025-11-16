package com.minechinima.operator.config.modal;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.modal.IPropertyModifierReleasableCommand;

/**
 * Configuraton for a property modifier command that can react to release event.
 * @param <P>
 */
public abstract class PropertyModifierReleaseableConfig<P extends OperatorPropertiesGroupStruct> extends PropertyModifierConfig<P> {
    public final BooleanValue toggleOnRelease;

    public PropertyModifierReleaseableConfig(String title, String description) {
        this(title, description, false);
    }

    public PropertyModifierReleaseableConfig(String title, String description, boolean releaseDefault) {
        super(title, description);
        this.addChild("toggle_on_release", this.toggleOnRelease = new BooleanValue("toggle_on_release", releaseDefault));
    }

    @Override
    public abstract IPropertyModifierReleasableCommand<P, ?> factory();
}
