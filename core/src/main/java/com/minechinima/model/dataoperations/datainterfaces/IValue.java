package com.minechinima.model.dataoperations.datainterfaces;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.dataoperations.operations.IDataOperation;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import org.jetbrains.annotations.Nullable;

/**
 * A more general interface for a {@link BaseStructure} that has a value that can change.
 * This interface exposes methods that can be used to undo such a data operation.
 * @implNote This decouples the specific class definition from the data operation.
 * The specific {@link IDataOperation} will only need to check
 * if this interface is implemented.
 */
public interface IValue<T> {
    /**
     * @return a serialized value that can be parsed again by {@link #valueFromData(BaseType)}
     */
    BaseType<?> valueToData();

    /**
     * @param data
     * @return a value that could be set to this
     * @throws BaseTypeCannotBeParsed to differentiate between an on purpose null
     */
    @Nullable
    T valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed;

    /**
     * This method should be overridden to define rules for data modification with app domain knowledge.
     * @param newValue
     * @return true if the method {@link #set(T)} can be called. This includes type checks!
     * @throws UserException throws meaningful localized exception for the user if the value cannot be set.
     * @implNote parameter is Object type because the data operation interfaces use these methods, and they have no
     * knowledge about the generic types.
     * <br><br>
     * Default implementation here because not every subclass should need to override only to do an "instance of" check.
     * The type-safety of calling {@link #valueFromData(BaseType)} with type T and then {@link #set(T)}
     * should be enough in most cases.
     */
    default boolean canSet(Object newValue) throws UserException {
        return true;
    }
    void set(T value);
}
