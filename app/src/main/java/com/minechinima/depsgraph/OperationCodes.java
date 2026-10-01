package com.minechinima.depsgraph;

public enum OperationCodes implements OperationNode.OperationCode {
    TRANSFORM_ENTRY,
    TRANSFORM_LOCAL,
    TRANSFORM_PARENT,
    TRANSFORM_EXIT,
    CONSTRAINTS_ENTRY,
    CONSTRAINTS_EVAL,
    CONSTRAINTS_EXIT,
    ;

    @Override
    public String getName() {
        return this.name();
    }
}
