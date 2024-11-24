package com.minechinima.model.base.dummies;

import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.utils.ValuePath;

public class DummyBaseHierarchyRoot extends BaseHierarchyRoot {
    public DummyBaseHierarchyRoot(String id, ValuePath.Format pathFormat) {
        super(id, pathFormat);
    }

    public DummyBaseHierarchyRoot(String id) {
        super(id, ValuePath.Format.PATH);
    }

    public DummyBaseHierarchyRoot _addChild(String name, BaseStructure child) {
        super.addChild(name, child);
        return this;
    }
}
