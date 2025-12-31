package com.minechinima.operator.config.modal;

import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.modal.IPropertyModifierCommand;
import com.minechinima.ui.utils.ILangKeyProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Command that modifies the properties of an Operator on a key map event.<br>
 * Example use case: Modal Operator to move an object -> while moving you can press X to only move on the X axis. This will
 * call this class which will set the corresponding property.
 * <br><br>
 * Is part of data model to allow configuration attributes<br>
 * Modifier to adjust dragging increments: float value to configure increment steps
 * <br><br>
 * Reasoning splitting operator up into operator commands and separate modifier commands:<br>
 * More transparency how the behavior of the operator is influenced.<br>
 * The Operator does not need to implement any hotkey logic, only logic based on the attributes.<br>
 * Higher flexibility with hotkey configuration.
 * @param <P>
 */
public abstract class PropertyModifierConfig<P extends OperatorProperties> extends ValueGroup implements ILangKeyProvider {
    private final String title;
    private final String description;

    public PropertyModifierConfig(String title, String description) {
        super("");
        this.title = title;
        this.description = description;
    }

    public abstract IPropertyModifierCommand<P, ?> factory();

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public @Nullable String tooltip() {
        return this.description;
    }
}
