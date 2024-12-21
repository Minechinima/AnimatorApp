package com.minechinima.model.logging.operations;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.base.BaseStructure;

/**
 * Interface for data operations that can be performed on an object.
 * Used for undo/redo and network sync.
 */
public interface IDataOperation<T> {
    /**
     * Perform this data operation on the given object.
     * @param context the object on which the data operation should be performed
     * @return true if the operation was successful.<br>
     * This can also be true if the operation was not able to perform the change,
     * but the state it would have created is already present i.e., it was virtually performed.
     * <br><br>
     * Example: DeleteOperation wants to delete a list element,
     * but that list element is already not present i.e. virtually deleted.
     * <br><br>
     * <b>Rationale:</b> The BaseStructure system is very strict, it never allows creating states it doesn't want.
     * This means a data operation performed on it can result in other changes (like clearing pointers).
     * <br><br>
     * In a chain of recorded IDataOperations applied on the tree,
     * one IDataOperation could lead to other automatic changes, triggered by the tree to ensure core data integrity.
     * <br><br>
     * If this happens in a chain of recorded IDataOperations,
     * the next operations in the chain would try to apply these tree correcting changes again, since they were recorded.
     * However, these changes are already applied to the tree as it triggered them automatically.
     * The chain of IDataOperations should not fail.<br><br>
     * If it fails, network syncing would never be possible as the user
     * can only record all IDataOperations in their tree and send them to the server where it applies all of them.
     * @throws UserException exception with a meaningful user message.
     * @apiNote This method could also throw other runtime exceptions.
     * @implNote argument type is generic to decouple this further more from the BaseStructures.
     * It is not necessary to provide a type here as the implementation of the interface will know how to deal with it.
     */
    boolean performOperation(T context) throws UserException;
    /**
     * Undo this operation on the given object.
     * @param context the object where this data operation should be undone.
     * @return true if the operation was successful.<br>
     * This can also be true if the operation was not able to perform the change,
     * but the state it would have created is already present i.e., it was virtually performed.
     * <br><br>
     * Example: DeleteOperation wants to delete a list element,
     * but that list element is already not present i.e. virtually deleted.
     * <br><br>
     * <b>Rationale:</b> The BaseStructure system is very strict, it never allows creating states it doesn't want.
     * This means a data operation performed on it can result in other changes (like clearing pointers).
     * <br><br>
     * In a chain of recorded IDataOperations applied on the tree,
     * one IDataOperation could lead to other automatic changes, triggered by the tree to ensure core data integrity.
     * <br><br>
     * If this happens in a chain of recorded IDataOperations,
     * the next operations in the chain would try to apply these tree correcting changes again, since they were recorded.
     * However, these changes are already applied to the tree as it triggered them automatically.
     * The chain of IDataOperations should not fail.<br><br>
     * If it fails, network syncing would never be possible as the user
     * can only record all IDataOperations in their tree and send them to the server where it applies all of them.
     * @throws UserException exception with a meaningful user message.
     * @apiNote This method could also throw other runtime exceptions.
     * @implNote argument type is generic to decouple this further more from {@link BaseStructure}.
     * It is not necessary to provide a type here as the implementation of the interface will know how to deal with it.
     */
    boolean undo(T context) throws UserException;

    /**
     * @param object
     * @return true if the given object can be handled by this.
     */
    boolean isCorrectType(Object object);
    boolean merge(IDataOperation<?> next);
    Type getOperationType();
}
