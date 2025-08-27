package com.minechinima.model.logging.operations;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.datainterfaces.IDMutable;

public class IdentifierChange implements IDataOperation<IDMutable> {
    private final String oldIdentifier;
    private String newIdentifier;
    private final String uniquePath;

    public <T extends BaseStructure & IDMutable> IdentifierChange(BaseStructure context, String oldIdentifier) {
        this.oldIdentifier = oldIdentifier;
        this.newIdentifier = context.getIdentifier();
        this.uniquePath = context.getFullPath().toUniquePath();
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
}
