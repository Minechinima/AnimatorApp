package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.*;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class GenericComponentBuilder<T extends BaseStructure> extends ComponentBuilder<T> {
    private final OperationCodes entryCode;
    private final OperationCodes exitCode;

    public GenericComponentBuilder(ComponentNode.ComponentType key, OperationCodes entryCode, OperationCodes exitCode) {
        super(key);
        this.entryCode = entryCode;
        this.exitCode = exitCode;
    }

    public GenericComponentBuilder(ComponentNode.ComponentType key) {
        this(key, OperationCodes.GENERIC_ENTRY, OperationCodes.GENERIC_EXIT);
    }

    @Override
    public void addEntryExitOperation(ComponentNode<T> component) {
        component.setEntry(component.addOperation(null, this.entryCode));
        component.setExit(component.addOperation(null, this.exitCode));
    }
}
