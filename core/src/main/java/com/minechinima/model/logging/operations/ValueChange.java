package com.minechinima.model.logging.operations;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;

public class ValueChange implements IDataOperation<IValue<?>> {
    private final BaseType<?> newValue;
    private final BaseType<?> oldValue;
    /**
     * Used to check if data should be merged
     */
    private final String path;

    public <T extends BaseStructure & IValue> ValueChange(T value, BaseType<?> oldValue) {
        this.oldValue = oldValue;
        this.newValue = value.valueToData(value.get());
        this.path = value.getFullPath().toUniquePath();
    }

    public <T extends BaseStructure & IValue> ValueChange(ValueChange current, ValueChange next) {
        this.oldValue = current.oldValue;
        this.newValue = next.newValue;
        this.path = current.path;
    }

    /**
     * @return path of node this change operation is executed on
     */
    public String getPath() {
        return this.path;
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
    public Type getOperationType() {
        return Type.UPDATE;
    }
}
