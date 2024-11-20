package com.minechinima.model.dataoperations.operations;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.dataoperations.datainterfaces.IValueList;

import java.util.UUID;

public class ListValueInsert implements IDataOperation<IValueList<?>> {
    private final UUID insertedUUID;

    public ListValueInsert(UUID insertedUUID) {
        this.insertedUUID = insertedUUID;
    }

    @Override
    public boolean performOperation(IValueList context) throws UserException {
        return false;
    }

    @Override
    public boolean undo(IValueList<?> context) {
        if (context.canRemove(this.insertedUUID)) {
            context.removeByListUUID(this.insertedUUID);
            return true;
        }
        return false;
    }

    @Override
    public boolean isCorrectType(Object object) {
        return object instanceof IValueList<?>;
    }

    @Override
    public boolean merge(IDataOperation<IValueList<?>> next) {
        return false;
    }
}
