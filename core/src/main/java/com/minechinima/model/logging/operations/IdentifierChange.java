package com.minechinima.model.logging.operations;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.datainterfaces.IDMutable;

public class IdentifierChange implements IDataOperation<IDMutable> {
    private final String oldIdentifier;

    public IdentifierChange(String oldIdentifier) {
        this.oldIdentifier = oldIdentifier;
    }

    @Override
    public boolean performOperation(IDMutable context) throws UserException {
        return false;
    }

    @Override
    public boolean undo(IDMutable context) throws UserException {
        context.setID(this.oldIdentifier);
        return context instanceof IDMutable;
    }

    @Override
    public boolean isCorrectType(Object object) {
        return object instanceof IDMutable;
    }

    @Override
    public Type getOperationType() {
        return Type.UPDATE;
    }

    @Override
    public boolean merge(IDataOperation next) {
        return false;
    }
}
