package com.minechinima.utils;

import org.jetbrains.annotations.NotNull;

/**
 * For a registry that can provide a default value as a fallback.
 * @param <T>
 */
//TODO get rid of default config, implement null properly -> a newly created hotkey should be unset
public interface IGenericDefaultRegistry<T> extends IGenericRegistry<T> {
    /**
     * @return a new default value that can be used in case of null.
     */
    @NotNull
    T getNewDefault();
}
