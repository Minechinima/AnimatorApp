package com.minechinima.utils;

import org.jetbrains.annotations.Nullable;

import javax.naming.OperationNotSupportedException;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Interface for a generic registry that registers and instantiates objects with an integer id.
 * @param <T>
 */
public interface IGenericRegistry<T> {
    /**
     * @param id
     * @return a newly created instance that was registered to the id.
     */
    @Nullable
    T getNew(int id);
    /**
     * @return the id that can be used to instantiate the instance of the given argument.
     * null if not registered.
     */
    @Nullable
    Integer getID(T value);

    /**
     * @return a new instance for every registered class
     */
    Map<Integer, T> getAllNew();

    /**
     * Register the instantiation of the given class to the given id.
     * @param constructor provide a new instance.
     * @param <J>
     * @throws IllegalArgumentException when the id is already registered.
     */
    <J extends T> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException;
}
