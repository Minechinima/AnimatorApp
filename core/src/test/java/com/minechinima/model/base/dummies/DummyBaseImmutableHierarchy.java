package com.minechinima.model.base.dummies;

import com.minechinima.model.base.BaseImmutableValueHierarchy;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.utils.ValuePath;

import java.util.UUID;

public class DummyBaseImmutableHierarchy extends BaseImmutableValueHierarchy<BaseStructure> {
    public DummyBaseImmutableHierarchy(String id, ValuePath.Format pathFormat) {
        super(id, pathFormat);
    }

    protected DummyBaseImmutableHierarchy(String id, UUID uuid, ValuePath.Format pathFormat) {
        super(id, uuid, pathFormat);
    }

    public void _add(String name, BaseStructure child) {
        super.addChild(name, child);
    }
}
