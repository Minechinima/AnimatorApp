package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.MapType;
import com.minechinima.model.base.BaseImmutableValueHierarchy;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A hierarchy element that can be used as a root as it does not permit setting a parent.
 */
public abstract class BaseHierarchyRoot extends BaseImmutableValueHierarchy<BaseStructure> {
    public BaseHierarchyRoot(String id, ValuePath.Format pathFormat) {
        super(id, pathFormat);
    }

    protected BaseHierarchyRoot(String id, UUID uuid, ValuePath.Format pathFormat) {
        super(id, uuid, pathFormat);
    }

    @Override
    final void throwExceptionForIllegalParentChange() throws SetParentNotAllowed {
        throw new SetParentNotAllowed("Setting the parent of a root element is not allowed.");
    }
}
