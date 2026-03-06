package com.minechinima.operator.ot;

import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.ot.commands.IModalOperator;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import com.minechinima.utils.IGenericRegistry;

/**
 * Interface for OperatorTypes to define modal modifier config for the config engine to automatically build.
 * @param <O>
 * @param <P>
 */
public interface IOTModalModifierProvider<C extends IContext, O extends IPropertyOperator<C, P> & IModalOperator<C>, P extends OperatorProperties>
        extends IOTModal<C, O>, IOTPropertyProvider<O, P> {
    /**
     * @return the operator property type the modifiers are for.
     */
    Class<P> getPropertyType();

    /**
     * @return the registry of available property modifiers. Can be the same registry as other already registered providers
     * if they share the same property type.
     */
    IGenericRegistry<PropertyModifierCommandConfig<P>> modifierRegistry();

    /**
     * @return the id to use for creating the modifier configuration category for the property type.
     */
    String modifierConfigIdentifier();
}
