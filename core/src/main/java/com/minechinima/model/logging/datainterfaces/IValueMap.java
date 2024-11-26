package com.minechinima.model.logging.datainterfaces;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.MapType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Map interface for map data operations
 * @param <T>
 */
public interface IValueMap<T> {
    @NotNull
    T valueFromData(MapType data) throws BaseTypeCannotBeParsed;
    MapType valueToData(T value);

    /**
     * This method should be overridden to define rules for data modification with app domain knowledge.
     * @param value
     * @return true if the method {@link #add(T)} can be called. This includes type checks!
     * @throws UserException throws meaningful localized exception for the user
     * @implNote parameter is Object type because the data operation interfaces use these methods, and they have no
     * knowledge about the generic types.
     * <br><br>
     * Default implementation here because not every subclass should need to override only to do an "instance of" check.
     * The type-safety of calling {@link #valueFromData(MapType)} with type T and then {@link #add(T)}
     * should be enough in most cases.
     */
    default boolean canAdd(Object value) throws UserException {
        return true;
    }
    /**
     *
     * @param value
     * @return true if the value could be added.
     */
    boolean add(@NotNull T value);

    /**
     * This method should be overridden to define rules for data modification with app domain knowledge.
     * @return true if the method {@link #remove(UUID)} can be called.
     */
    default boolean canRemove(UUID uuid) throws UserException {
        return true;
    }

    /**
     * This method should be overridden to define rules for data modification with app domain knowledge.
     * @param id
     * @return true if the method {@link #remove(String)} can be called.
     * @throws UserException
     */
    default boolean canRemove(String id) throws UserException {
        return true;
    }

    /**
     * @return the previous value associated with the uuid, or null if there was no mapping for the uuid.
     */
    @Nullable
    T remove(UUID uuid);
    /**
     * @return the previous value associated with the identifier, or null if there was no mapping for the identifier.
     */
    @Nullable
    T remove(String identifier);

    boolean contains(String id);
    boolean contains(UUID id);
}
