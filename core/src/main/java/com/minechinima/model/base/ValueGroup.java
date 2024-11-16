package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.data.types.MapType;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.dataoperations.datainterfaces.IDMutable;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

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
