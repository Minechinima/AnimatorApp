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
 * <b>Important Design Rule: ValueGroups never change their tree structure after being constructed!
 * The tree structure of ValueGroups needs to be predictable and stay the same throughout the lifetime of the object.</b>
 * //TODO how do we ensure pointer namespaces are elements that can't be removed from the tree?
 *    This again is the same property of the system: Storing references to treenodes is dangerous, as you must always expect that the treenode
 *    could be removed from the tree which must result in a clean up of memory!
 */
public abstract class ValueGroup extends BaseValueHierarchy<BaseStructure> {
    private final List<BaseStructure> children = new ArrayList<>();

    public ValueGroup(String id) {
        super(id, ValuePath.Format.PATH);
    }

    protected ValueGroup(String id, UUID uuid) {
        super(id, uuid, ValuePath.Format.PATH);
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

    /**
     * Only adds the child if it has not been added before.
     */
    protected void add(BaseStructure... children) {
        for (BaseStructure child : children) {
            if (this.children.contains(child)) continue;

            if (child instanceof IDMutable mutable && this.isUUIDPresent(mutable.getUUID())) {
                throw new IllegalArgumentException("One of the BaseStructures to be inserted has a UUID " +
                        "that already exists in the children.");
            }

            this.children.add(child);
            try {
                child.setParent(this);
            } catch (SetParentNotAllowed e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public MapType toData() {
        Map<String, BaseType<?>> map = new HashMap<>();

        for (BaseStructure child : this.children) {
            map.put(child.getIdentifier(), child.toData());
        }

        return new MapType(map);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void _fromData(BaseType<?> data) {
        MapType type;
        if ((type = data.asMapType()) == null) return;
        for (Map.Entry<String, BaseType<?>> entry : type) {
            /* only existing attributes can be serialised, so search for them here */
            for (BaseStructure child : this.children) {
                if (child.getIdentifier().equals(entry.getKey())) {
                    child.fromData(entry.getValue());
                    break;
                }
            }
        }
    }
}
