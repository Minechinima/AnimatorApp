package com.minechinima.model.base;

import com.minechinima.model.utils.ValuePath;

import java.util.*;

/**
 * This is the template class for classes that intend to register their class attributes as values for the value tree.
 * <b>The class attributes where the datatype is BaseStructure should be declared final, as they are registered to the tree.</b>
 */
public abstract class ValueGroup extends BaseImmutableValueHierarchy<BaseStructure> {
    public ValueGroup(String id) {
        super(id, ValuePath.Format.PATH);
    }

    protected ValueGroup(String id, UUID uuid) {
        super(id, uuid, ValuePath.Format.PATH);
    }
}
