package com.minechinima.model.dataoperations.datainterfaces;

import com.minechinima.data.types.MapType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.exceptions.UserException;

import java.util.UUID;

/**
 * Interface for list data operations.
 * A list always needs a UUID for every element to identify it independently of its index.
 * This is needed for the future possibility of collaborative working.
 * @param <T>
 */
public interface IValueList<T> {
    ListElement<T> listElementFromData(MapType child) throws BaseTypeCannotBeParsed;
    MapType listElementToData(ListElement<T> child);


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
    default boolean canAdd(int index, ListElement<Object> element) throws UserException {
        return true;
    }

    /**
     * @return true if the element could be added.
     * @throws IndexOutOfBoundsException
     * @throws NullPointerException when the value is null
     * @throws IllegalArgumentException when the same reference equal {@link ListElement#value} is in the list.
     */
    boolean addListElement(int index, ListElement<T> element);

    /**
     * This method should be overridden to define rules for data modification with app domain knowledge.
     * @param uuid
     * @return true if the method {@link #removeByListUUID(UUID)} can be called.
     */
    default boolean canRemove(UUID uuid) {
        return true;
    }
    /**
     * @param uuid
     * @return the element with the uuid, if it was present.
     */
    T removeByListUUID(UUID uuid);

    void moveByListUUID(UUID uuid, int newIndex);

    int size();
    boolean containsListUUID(UUID uuid);
}
