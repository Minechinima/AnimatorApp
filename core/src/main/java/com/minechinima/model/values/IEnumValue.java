package com.minechinima.model.values;

import com.minechinima.controller.api.defaults.ValueControllerFactory;
import com.minechinima.ui.utils.ILangKeyProvider;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Interface for a datastructure that acts like an enum.
 * Such a datastructure has a current value and provides an array of available values to choose from.
 * @param <T> extends {@link ILangKeyProvider} so the UI can automatically display text for the available "enum" values.
 */
public interface IEnumValue<T extends ILangKeyProvider> {
    /**
     * @return all values that can be set to the enum, mapped to an integer so the UI can identify which is selected.
     * A value can be null.
     */
    Map<Integer, T> getAvailableValues();

    /**
     * @return the index of the current value, see {@link #getAvailableValues()}
     */
    Integer getCurrentValue();
    ValueControllerFactory<T, ?> controllers();
}
