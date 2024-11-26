package com.minechinima.model.base.dummies;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseMutableValueHierarchy;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.utils.ValuePath;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DummyBaseMutableHierarchy extends BaseMutableValueHierarchy<BaseStructure> {
    public final Set<BaseStructure> children = new HashSet<>();

    public DummyBaseMutableHierarchy(String id, ValuePath.Format pathFormat) {
        super(id, pathFormat);
    }

    protected DummyBaseMutableHierarchy(String id, UUID uuid, ValuePath.Format pathFormat) {
        super(id, uuid, pathFormat);
    }

    @Override
    public BaseType<?> toData() {
        return null;
    }

    @Override
    public void fromData(BaseType<?> data) {

    }

    public DummyBaseMutableHierarchy add(BaseStructure... children) {
        for (BaseStructure child : children) {
            this.children.add(child);
            try {
                child.setParent(this);
            } catch (SetParentNotAllowed e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    @Override
    public boolean addChild(BaseStructure child) {
        return false;
    }

    @Override
    public boolean removeChild(BaseStructure child) {
        return false;
    }

    @Override
    public Collection<BaseStructure> getChildren() {
        return Set.copyOf(this.children);
    }

    @Override
    public boolean isDirectChild(BaseStructure child) {
        return this.children.contains(child);
    }
}
