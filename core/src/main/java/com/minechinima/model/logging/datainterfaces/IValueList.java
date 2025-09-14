package com.minechinima.model.logging.datainterfaces;

import com.minechinima.data.types.MapType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.exceptions.UserException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Interface for list data operations.
 * A list always needs a UUID for every element to identify it independently of its index.
 * This is needed for the future possibility of collaborative working.
 * @param <T>
 */
public interface IValueList<T> {
    /**
     * @throws BaseTypeCannotBeParsed if the data is not in the correct format.<br>
     * Important to throw an exception, so the caller of this method can interrupt the code if
     * no correct value can be returned by this method. This allows more transparent failure analysis
     * (see undo redo system failing to revert changes when the value cannot be parsed).
     */
    @Contract(pure = true)
    @NotNull
    ListElement<T> listElementFromData(@NotNull MapType child) throws BaseTypeCannotBeParsed;

    @Contract(pure = true)
    @NotNull
    MapType listElementToData(@NotNull ListElement<T> child);


    /**
     * This method should be overridden to define rules for data modification with app domain knowledge.
     * @param index
     * @param element
     * @return true if the method {@link #addListElement(int, ListElement)} can be called. This includes type checks!
     * @throws UserException throws meaningful localized exception for the user if the add operation should not be performed.
     *
     * @implNote parameter is Object type because the data operation interfaces use these methods, and they have no
     * knowledge about the generic types.
     * <br><br>
     * Default implementation here because not every subclass should need to override only to do an "instance of" check.
     * The type-safety of calling {@link #listElementFromData(MapType)} with type T and then {@link #addListElement(int, ListElement)}
     * should be enough in most cases.
     */
    @Contract(pure = true)
    default boolean canAdd(int index, @NotNull ListElement<Object> element) throws UserException {
        return true;
    }

    /**
     * @return true if the element could be added.
     * @throws IndexOutOfBoundsException
     * @throws NullPointerException when the value is null
     * @throws IllegalArgumentException when the same reference equal {@link ListElement#value} is in the list.
     */
    @Contract(pure = false)
    boolean addListElement(int index, @NotNull ListElement<T> element);

    /**
     * This method should be overridden to define rules for data modification with app domain knowledge.
     * @param uuid
     * @return true if the method {@link #removeByListUUID(UUID)} can be called.
     */
    @Contract(pure = true)
    default boolean canRemove(@NotNull UUID uuid) {
        return true;
    }

    /**
     * @param uuid
     * @return the element with the uuid, if it was present.
     */
    @Contract(pure = false)
    @Nullable
    T removeByListUUID(@NotNull UUID uuid);

    @Contract(pure = false)
    void moveByListUUID(@NotNull UUID uuid, int newIndex);

    @Contract(pure = true)
    int size();

    @Contract(pure = true)
    boolean containsListUUID(@NotNull UUID uuid);

    /**
     * @param element
     * @return the list uuid of the element, null if there is no such element in the list.
     */
    @Nullable
    @Contract(pure = true)
    UUID listUUIDOf(T element);

    /**
     * @param element
     * @return true if the list contains the given element
     */
    @Contract(pure = true)
    boolean contains(T element);
}
