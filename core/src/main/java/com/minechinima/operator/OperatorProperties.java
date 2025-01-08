package com.minechinima.operator;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.values.base.ValueGroup;

public class OperatorProperties extends ValueGroup {
    public OperatorProperties(String id) {
        super(id);
    }

    public void copyFrom(OperatorProperties other) {
        this.fromData(other.toData());
    }
}
