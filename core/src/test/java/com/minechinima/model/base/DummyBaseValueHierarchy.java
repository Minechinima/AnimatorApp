package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@TestOnly
final class DummyBaseValueHierarchy extends BaseValueHierarchy<BaseStructure> {
    public final List<BaseStructure> children = new ArrayList<>();

    DummyBaseValueHierarchy(String id, ValuePath.Format format) {
        super(id, format);
    }

    /**
     * This is final to ensure no subclasses violate the contract of children having this as parent. This could cause
     * bugs with path collisions algorithms, etc.
     */
    @Override
    public final List<BaseStructure> getChildren() {
        return new ArrayList<>(this.children);
    }

    @Override
    public boolean isDirectChild(BaseStructure child) {
        return this.children.contains(child);
    }

    protected void remove(BaseStructure child) {
        if (this.children.contains(child)) {
            child.setParentNull();
            this.children.remove(child);
        }
    }

    /**
     * Only adds the child if it has not been added before.
     */
    protected DummyBaseValueHierarchy add(BaseStructure... children) {
        for (BaseStructure child : children) {
            if (this.children.contains(child)) return this;

            this.children.add(child);
            try {
                child.setParent(this);
            } catch (SetParentNotAllowed e) {
                this.children.remove(child);
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public BaseType toData() {
        return null;
    }

    @Override
    public void fromData(BaseType data) {

    }
}
