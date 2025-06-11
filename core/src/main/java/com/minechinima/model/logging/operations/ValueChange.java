package com.minechinima.model.logging.operations;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;

public class ValueChange implements IDataOperation<IValue<?>> {
    private BaseType<?> newValue;
    private final BaseType<?> oldValue;
    /**
     * Used to check if data should be merged
     */
    private final String path;

    public <T extends BaseStructure & IValue> ValueChange(T value, BaseType<?> oldValue) {
        this.oldValue = oldValue;
        this.newValue = value.valueToData();
        this.path = value.getFullPath().toUniquePath();
    }

    @Override
    public boolean performOperation(IValue<?> context) throws UserException {
        return false;
    }

    @Override
    public boolean undo(IValue change) throws UserException {
        try {
            Object value = change.valueFromData(this.oldValue);
            if (change.canSet(value)) {
                change.set(value);
                return true;
            }
        } catch (BaseTypeCannotBeParsed e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public boolean isCorrectType(Object object) {
        return object instanceof IValue<?>;
    }

    @Override
    public boolean canBeParallel(IDataOperation<?> test) {
        return test.getOperationType() == Type.UPDATE;
    }

    @Override
    public Type getOperationType() {
        return Type.UPDATE;
    }

    @Override
    public boolean merge(IDataOperation next) {
        if (!(next instanceof ValueChange nextChange && nextChange.path.equals(this.path))) {
            return false;
        }

        this.newValue = nextChange.newValue;

        return true;
    }
}
