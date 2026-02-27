package com.minechinima.depsgraph;

public enum OperationCodes implements OperationNode.OperationCode {
    GENERIC_ENTRY,
    GENERIC_EXIT;

    @Override
    public String getName() {
        return this.name();
    }
}
