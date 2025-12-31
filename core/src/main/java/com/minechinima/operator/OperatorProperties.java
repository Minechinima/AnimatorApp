package com.minechinima.operator;

import com.minechinima.model.values.base.ValueGroup;

public abstract class OperatorProperties extends ValueGroup {
    public OperatorProperties() {
        super("operator_properties");
    }

    public final void copyFrom(OperatorProperties other) {
        this.fromData(other.toData());
    }
}
