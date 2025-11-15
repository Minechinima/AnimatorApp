package com.minechinima.model.base;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.MapType;
import com.minechinima.model.base.utils.BaseOperationsUtils;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.logging.datainterfaces.IDMutable;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A base for all hierarchies that are static and cannot change children after init.<br><br>
 * {@link IDMutable} is not allowed as direct child.
 * @param <T>
 */
public non-sealed abstract class BaseImmutableValueHierarchy<T extends BaseStructure> extends BaseValueHierarchy<T> {
    /**
     * The actual children of this tree hierarchy.
     * The String key is for serialization.
     * This allows for empty identifiers of grouping nodes.
     */
    private final Set<T> children = Collections.newSetFromMap(new IdentityHashMap<>());
    /**
     * A buffer of children (ideally added during construction of the instance)
     * to allow initializing them once into the hierarchy.
     */
    private final BiMap<String, T> mappingBuffer = HashBiMap.create();
    private boolean initializedChildren = false;

    public BaseImmutableValueHierarchy(String id, ValuePath.IFormat pathFormat) {
        super(id, pathFormat);
    }

    protected BaseImmutableValueHierarchy(String id, UUID uuid, ValuePath.IFormat pathFormat) {
        super(id, uuid, pathFormat);
    }

    @Override
    void onParentChangeOfThis(@Nullable final BaseValueHierarchy<?> oldParent) throws SetParentNotAllowed {
        if (!this.initializedChildren && this.getParent() != null) {
            //TODO is this okay? Then we don't need to worry about cluttering the entire codebase full of initChildren calls
            this.initChildren();
            return;
        }
        /* for safety to prevent and debug when we forget to initialize a node before adding it */
        if (!this.initializedChildren && this.getParent() != null) {
            throw new SetParentNotAllowed("Cannot add a not initialized BaseImmutableValueHierarchy to the data tree.");
        }
    }

    /**
     * @return a copy of the children Set.
     * @implNote  this is final to ensure that the children are always immutable
     */
    @Override
    public final Set<T> getChildren() {
        return new HashSet<>(this.children);
    }

    /**
     * @param child
     * @return true if this child is contained in the children of this.
     * @implNote this is final to ensure that the children are always immutable
     */
    @Override
    public final boolean isDirectChild(BaseStructure child) {
        return this.children.contains(child);
    }

    public final boolean isInitialized() {
        return this.initializedChildren;
    }

    /**
     * Add the given children into a buffer. The buffer can be flushed into the children <b>once</b> with {@link #initChildren()}.
     * This should be done outside the constructor, after "<i>new CLASS()</i>" is finished.
     * @param name a unique name to identify the child independent of the child's identifier.
     *             This allows the child to have an empty identifier but still be found by the serialization algorithms.
     * @param child the child to add.
     */
    protected final void addChild(String name, T child) {
        if (this.initializedChildren) {
            throw new UnsupportedOperationException("The children are already initialized.");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Empty names are not allowed");
        }

        /*
         * UUID is final in BaseStructure.
         * The parsing and setting of UUID would need to happen in the constructor to deserialize properly.
         * However, deserialization only makes sense after construction of the object so all children are added.
         * Also, this enforces the concept of an immutable hierarchy???
         */
        if (child instanceof IDMutable) {
            throw new UnsupportedOperationException("Adding IDMutable to an ImmutableHierarchy is not supported and can have unwanted side effects.");
        }

        if (this.mappingBuffer.containsKey(name)) {
            throw new IllegalArgumentException("The key was already registered for the children.");
        }

        if (this.mappingBuffer.containsValue(child)) {
            throw new IllegalArgumentException("The value was already registered for the children. Duplicates are not allowed.");
        }

        this.mappingBuffer.put(name, child);
    }

    /**
     * Adds the children, that have been buffered during object creation, to the tree.
     */
    public final void initChildren() {
        if (this.initializedChildren) {
            throw new UnsupportedOperationException("The children are already initialized.");
        }

        this.initializedChildren = true;

        for (Map.Entry<String, T> mapping : this.mappingBuffer.entrySet()) {
            if (mapping.getValue() instanceof BaseImmutableValueHierarchy<?> ih && !ih.isInitialized()) {
                //TODO initChildren call here too, temporarily to keep original code
                ih.initChildren();
                //throw new RuntimeException("Trying to add a not initialized BaseImmutableValueHierarchy mapping{" + mapping.getKey() + "}");
            }
            BaseOperationsUtils.addChildOrThrow(mapping.getValue(), this, this.children);
        }
    }

    @Override
    public final MapType toData() {
        Map<String, BaseType<?>> map = new HashMap<>();

        for (Map.Entry<String, T> child : this.mappingBuffer.entrySet()) {
            map.put(child.getKey(), child.getValue().toData());
        }

        return new MapType(map);
    }

    @Override
    public final void fromData(BaseType<?> data) {
        MapType type;
        if ((type = data.asMapType()) == null) return;
        for (Map.Entry<String, BaseType<?>> entry : type) {
            T child;
            if ((child = this.mappingBuffer.get(entry.getKey())) == null
                /* TODO allow deserialization of not initialized || !this.children.contains(child)*/) {
                continue;
            }
            child.fromData(entry.getValue());
        }
    }
}
