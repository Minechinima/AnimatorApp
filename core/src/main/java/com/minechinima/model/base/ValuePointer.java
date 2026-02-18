package com.minechinima.model.base;

import com.minechinima.controller.factories.IControllerFactoryProvider;
import com.minechinima.controller.factories.ValueControllerFactory;
import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.NullType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.exceptions.PathParsingException;
import com.minechinima.model.memory.MemoryLeakError;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.utils.IValuePointerConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * This is a value that points to another element in the tree, where this node is located in.<br>
 * It acts like a real pointer, if the target element is deleted, this pointer is automatically set to null.<br>
 * The pointer uses unique paths to uniquely address a target independent of possible identifier changes.<br>
 * This value is not going to serialize and store the target itself, it only points to it with a path.
 * @param <T>
 */
public class ValuePointer<T extends BaseStructure> extends BaseStructure implements IValue<T>, IControllerFactoryProvider<ValueControllerFactory<T, ?>> {
    /**
     * When deserialized and the pointer target cannot be found, this will be set to the last known path.
     */
    private String cachedUniquePath;
    @Nullable
    private T referenced;
    private final BaseValueHierarchy<? extends BaseStructure> referenceFrame;
    /**
     * True when the reference frame was checked and is correct.
     */
    private boolean initialized;
    /**
     * The type of this ValuePointer target.
     * @implNote important because generics suck :3
     */
    private final Class<T> type;
    private final ValueControllerFactory<T, ValuePointer<T>> controller = new ValueControllerFactory<>(this);

    public ValuePointer(String id, Class<T> type, @NotNull BaseValueHierarchy<? extends BaseStructure> referenceFrame) {
        super(id);

        if (referenceFrame == null) {
            throw new IllegalArgumentException("Pointer reference frame cannot be null.");
        }

        this.referenceFrame = referenceFrame;
        this.type = type;
    }

    public ValueControllerFactory<T, ValuePointer<T>> controller() {
        return this.controller;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Following cases are not allowed for changing parent of a ValuePointer:
     * <li>
     *     <ul>changing the parent when the reference frame would become illegal.</ul>
     *     <ul>changing the parent when it is constrained and this reference value is not allowed.</ul>
     * </li>
     * @param oldParent
     * @throws SetParentNotAllowed
     */
    @Override
    public final void onParentChangeOfThis(@Nullable final BaseValueHierarchy<?> oldParent) throws SetParentNotAllowed {
        this.onAnyParentChange(this.getParent());
    }

    /**
     * Automatically try to initialize the pointer and also check if the pointer is still valid if it was
     * initialized and the tree structure changes again.
     */
    @Override
    final void onParentChangeInParents(@NotNull BaseValueHierarchy<?> affectedNode,
                                       @Nullable final BaseValueHierarchy<?> oldParent) throws SetParentNotAllowed {
        this.onAnyParentChange(affectedNode.getParent());
    }

    private void onAnyParentChange(BaseValueHierarchy<?> newParent) throws SetParentNotAllowed {
        if (newParent == null) return;
        try {
            this.tryInitOrthrowExceptionIllegalReferenceFrame();
            this.checkConstraintsForValueAndThrow(this.referenced, false);
        } catch (Exception e) {
            throw new SetParentNotAllowed(e.getMessage());
        }
        /*
         * We cannot add a pointer to the tree with an invalid value, but we cannot set the value
         * if it is not added to the tree.
         * When we add the pointer to a tree we need to add the observer, as a pointer can be set
         * without being in the tree.
         */
        if (this.referenced != null) this.referenced.addPointer(this);
    }

    private void tryInitOrthrowExceptionIllegalReferenceFrame() throws IllegalStateException {
        /*
         * A pointer can only point to values that are in the same tree as this pointer.
         * Imagine a pointer in an OS trying to point into inaccessible memory...
         * Another important Reason: we want to record all changes to a tree. If a pointer could point to a value in a
         * completely separate tree, we cannot record the change that resulted in clearing this pointer.
         * This would break the core design principle of the data tree that everything is fully revert-able and recordable.
         */
        if (!this.isInSameTree(this.referenceFrame)) {
            /* pointer was initialized and parent changed, now it could become invalid */
            if (this.initialized) {
                throw new IllegalStateException("Pointer reference frame is not in the same tree.");
            }
            return;
        }

        /*
         * We need to go through the parent and check if the reference frame could be removed
         * and how the tree structure is built.
         */
        List<BaseValueHierarchy<?>> mutable = this.referenceFrame.findFirstParentTrace(
                parent -> parent instanceof BaseMutableValueHierarchy<? extends BaseStructure>);
        if (mutable == null) {
            /* reference frame will always be in the tree, pointer can always refer to it */
            this.initialized = true;
        } else {
            /*
             * with size==1: first parent of reference frame is already mutable, this means the pointer needs to be a child
             * of the reference frame to make up a closed system.
             */
            if (mutable.size() == 1 && !this.isChildOf(this.referenceFrame)) {
                throw new IllegalStateException("The pointer reference frame could be removed from the tree " +
                        "and the pointer is not a child of the frame.");
            } else if (mutable.size() >= 2) {
                /*
                 * When the reference frame is a distant child of a mutable hierarchy,
                 * the pointer must be at least a child of the first node that is immutable again.
                 */
                BaseValueHierarchy<?> test = mutable.get(mutable.size() - 2);
                /* safety first...*/
                if (test instanceof BaseMutableValueHierarchy<?>) {
                    throw new IllegalStateException("Some weird programmer bug...");
                }

                if (!this.isChildOf(test)) {
                    throw new IllegalStateException("The pointer reference frame could be removed from the tree " +
                            "and the pointer is not a child of the subtree.");
                }
            } else if (mutable.isEmpty()) {
                throw new IllegalStateException("Parent trace list is empty, this is unexpected.");
            }

            /* this can only be reached if no invalid state is present as those cause an exception */
            this.initialized = true;
        }
    }

    public Class<T> getType() {
        return this.type;
    }

    public BaseValueHierarchy<? extends BaseStructure> getReferenceFrame() {
        return this.referenceFrame;
    }

    public String getCachedUniquePath() {
        return this.cachedUniquePath;
    }

    public void setNullPointer() {
        this.set(null);
    }

    @Override
    public void set(@Nullable T value) {
        if (value != null && !this.type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Cannot set the value to this pointer as the type is not compatible.");
        }

        //TODO get rid of referenceframes - use global paths and root, and just
        /*
         * Allow setting the value when it is not in the tree,
         * as then it can't hurt tree structure with an illegal reference frame.
         * This needs to be allowed to set the pointer to something valid when adding it to
         * a constrained parent.
         * Example: Parent is constrained and does not allow null pointers,
         * then this needs to be != null before added to the tree.
         */
        if (!this.initialized && this.getParent() != null) {
            this.tryInitOrthrowExceptionIllegalReferenceFrame();
            if (!this.initialized) {
                throw new IllegalStateException("ValuePointer is not initialized.");
            }
        }

        if (this.referenced == value) {
            if (value == null) this.cachedUniquePath = null;
            return;
        }

        this.checkConstraintsForValueAndThrow(value, true);

        final T prev = this.referenced;
        final String prevCachedPath = this.cachedUniquePath;
        final ValuePath path = value != null ? value.getRelativePath(this.referenceFrame) : null;
        final String uniquePathNewReference = value != null && path != null ? path.toUniquePath() : null;
        /* safety checks when changing pointer reference */
        if (value != null) {
            if (!value.isChildOf(this.referenceFrame)) {
                throw new IllegalArgumentException("Cannot point to a node that is not a child of the pointer's reference frame.");
            }

            if (path == null ||
                    this.referenceFrame.getChildByUniquePath(uniquePathNewReference) != value) {
                throw new IllegalArgumentException("Cannot point to a node as it is not addressable with a path.");
            }
        }

        this.mutate(() -> {
            this.referenced = value;
            if (value == null) {
                this.cachedUniquePath = null;
            } else {
                /* can only perform the listening operations when we are in the same tree */
                this.referenced.addPointer(this);

                if (prev != null) {
                    prev.removePointer(this);
                }
                this.cachedUniquePath = uniquePathNewReference;
            }
            if (prev != null) prev.removePointer(this);
            return true;
        }, () -> {
            this.referenced = prev;
            this.cachedUniquePath = prevCachedPath;
            if (prev != null) prev.addPointer(this);
            if (value != null) value.removePointer(this);
            return true;
        }, () -> {
            if (value == null) {
                return new ValueChangeEvent(this, new StringType(prevCachedPath), prev, NullType.INSTANCE, null);
            } else {
                return new ValueChangeEvent(this, prev, value);
            }
        });
    }

    public T get() {
        if (this.referenced == null && this.cachedUniquePath != null) {
            T newValue = this.getTargetFromPath(this.cachedUniquePath);
            //TODO REALLY BAD! GETTER SHOULD NEVER PERFORM SET! DOCUMENT THIS AND REFACTOR!
            if (newValue != null) this.set(newValue);
        }
        return this.referenced;
    }

    /**
     * Check if the given value is allowed with the current parent and throw exceptions if setting this pointer
     * to the given value is illegal.
     * @param value the given value to test the constraints
     * @param executeCallback whether to execute the callbacks of the parent.
     *                        If false, an exception will be immediately thrown if the given value is not allowed by the parent.
     * @throws IllegalArgumentException
     */
    private void checkConstraintsForValueAndThrow(T value, boolean executeCallback) throws IllegalArgumentException {
        final BaseValueHierarchy<?> parent;
        if ((parent = this.getParent()) == null || !(parent instanceof IValuePointerConstraint constraint)) return;

        if (value == null && !constraint.nullPointerAllowed()) {
            if (executeCallback) {
                /* notify constraint of null pointer change */
                constraint.callbackOnNullPointer(this);
            }

            /*
             * if we are still part of the parent a null pointer is not allowed
             * Important: Parent defines what kind of pointers are allowed for it,
             * so the important criteria here is that the ValuePointer gets removed from
             * the immediate parent! Do not check whether the parent is removed from the tree.
             */
            if (this.getParent() == parent) {
                throw new IllegalArgumentException("Parent does not allow null pointers.");
            }
        }

        if (value != null && !constraint.duplicatePointerTargetsAllowed()) {
            if (this.testIfParentContainsTarget(value)) {
                if (executeCallback) {
                    constraint.callbackOnDuplicatePointerTarget(this);

                    /* if there are still duplicates, then it is only valid if we are not part of the parent anymore */
                    if (this.testIfParentContainsTarget(value) && this.getParent() == parent) {
                        throw new IllegalArgumentException("Parent does not allow duplicate pointer targets.");
                    }
                } else {
                    throw new IllegalArgumentException("Parent does not allow duplicate pointer targets.");
                }
            }
        }
    }

    private boolean testIfParentContainsTarget(T newValue) {
        if (this.getParent() == null) return false;
        Collection<? extends BaseStructure> childrenOfParent = this.getParent().getChildren();

        for (BaseStructure child : childrenOfParent) {
            if (child instanceof ValuePointer<?> ptr
                    && ptr != this && ptr.get() == newValue) {
                return true;
            }
        }

        return false;
    }

    @Override
    public StringType valueToData(T obj) {
        return new StringType(obj == null ? "" : obj.getRelativePath(this.referenceFrame).toUniquePath());
    }

    @Override
    public @Nullable T valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        //TODO serialization is just placeholder, we need to check nulls in serialization and how to deal with them
        StringType value;
        if ((value = data.asStringType()) == null) throw new BaseTypeCannotBeParsed("ValuePointer expects stringtype.");
        if (value.getValue() == null) return null;
        try {
            BaseStructure target = this.referenceFrame.getChildByUniquePath(new ValuePath(value.getValue()));
            if (target == null) {
                return null;
            } else if (this.type.isAssignableFrom(target.getClass())) {
                return (T) target;
            } else {
                throw new BaseTypeCannotBeParsed("The reference serialized from the path is not applicable to the pointer type.");
            }
        } catch (PathParsingException e) {
            throw new BaseTypeCannotBeParsed(e);
        }
    }

    protected @Nullable T getTargetFromPath(String path) {
        BaseStructure target = this.referenceFrame.getChildByUniquePath(path);
        if (target == null) {
            return null;
        } else if (this.type.isAssignableFrom(target.getClass())) {
            return (T) target;
        }

        return null;
    }

    @Override
    public StringType toData() {
        return this.valueToData(this.get());
    }

    @Override
    public void fromData(BaseType<?> data) {
        try {
            T newValue = this.valueFromData(data);
            String cachedPath = this.cachedUniquePath;
            this.set(newValue);
            if (newValue == null) {
                this.cachedUniquePath = cachedPath;
            }
        } catch (BaseTypeCannotBeParsed e) {
        }
    }
}