package com.minechinima.ui.utils;

import com.minechinima.controller.factories.IControllerFactoryProvider;
import com.minechinima.controller.factories.ValueControllerFactory;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.datainterfaces.IValue;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Interface for a datastructure that acts like an enum in the UI.<
 * Such a datastructure has a current value and provides an array of available values to choose from.
 * @param <T> extends {@link ILangKeyProvider} so the UI can automatically display text for the available "enum" values.
 */
public interface IUIEnumValue<T extends ILangKeyProvider, V extends BaseStructure & IValue<T>
        & IControllerFactoryProvider> extends IControllerFactoryProvider<ValueControllerFactory<T, V>> {
    /**
     * @return all values that can be set to the enum, mapped to an integer so the UI can identify which is selected.
     * A value can be null.
     */
    Map<Integer, T> getAvailableValues();

    /**
     * @return the index of the current value, see {@link #getAvailableValues()}, null if the value is not set.
     */
    @Nullable
    Integer getCurrentValueIndex();
    T getCurrentValue();
}
