package com.minechinima.model.dataoperations;

import com.minechinima.model.dataoperations.datainterfaces.IValueList;
import com.minechinima.model.exceptions.UserException;

import java.util.UUID;

public class ListChangeIndex implements IDataOperation<IValueList> {
    private final int oldIndex;
    private final UUID uuid;

    public ListChangeIndex(int oldIndex, UUID uuid) {
        this.oldIndex = oldIndex;
        this.uuid = uuid;
    }

    @Override
    public boolean performOperation(IValueList context) throws UserException {
        return false;
    }

    @Override
    public boolean undo(IValueList context) {
        context.moveByListUUID(this.uuid, this.oldIndex);
        return true;
    }

    @Override
    public boolean isCorrectType(Object object) {
        return object instanceof IValueList;
    }

    @Override
    public boolean merge(IDataOperation next) {
        return false;
    }
}
