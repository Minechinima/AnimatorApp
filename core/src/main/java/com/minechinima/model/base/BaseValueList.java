package com.minechinima.model.base;

import com.minechinima.model.utils.ValuePath;
import com.minechinima.data.types.*;
import com.minechinima.model.utils.ID;

import java.util.*;

public abstract class BaseValueList<T extends BaseStructure> extends BaseValueOrderedCollection<T> {
    public BaseValueList(String id) {
        super(id, ValuePath.Format.ARRAY);
    }

    protected BaseValueList(String id, UUID uuid) {
        super(id, uuid, ValuePath.Format.ARRAY);
    }

    @Override
    protected final ID getIDPrefixForChild(BaseStructure child) {
        int i = this.indexOf(child);
        if (i >= 0) return new ID(String.valueOf(i), this.listUUIDOfIndex(i));
        return null;
    }

    @Override
    protected final ValuePath.Format getPathFormatPrefixForChildren() {
        return ValuePath.Format.PATH;
    }
}