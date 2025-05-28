package com.minechinima.operator;

import com.minechinima.model.values.base.ValueGroup;

public abstract class OperatorPropertiesGroupStruct extends ValueGroup {
    public OperatorPropertiesGroupStruct(String id) {
        super(id);
    }

    public void copyFrom(OperatorPropertiesGroupStruct other) {
        this.fromData(other.toData());
    }

    /**
     * @return originalProps of this.
     */
    public abstract OperatorPropertiesGroupStruct copy();
}
