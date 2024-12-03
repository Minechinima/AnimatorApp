package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.exceptions.PathParsingException;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.memory.MemoryLeakError;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This is a value that points to another element in the tree, where this node is located in.<br>
 * It acts like a real pointer, if the target element is deleted, this pointer is automatically set to null.<br>
 * The pointer uses unique paths to uniquely address a target independent of possible identifier changes.<br>
 * This value is not going to serialize and store the target itself, it only points to it with a path.
 * @param <T>
 */
public class ValuePointer<T extends BaseStructure> extends BaseStructure implements IValue<T> {
    /**
     * When deserialized and the pointer target cannot be found, this will be set to the last known path.
     */
    private String cachedUniquePath;
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

    public ValuePointer(String id, Class<T> type, @NotNull BaseValueHierarchy<? extends BaseStructure> referenceFrame) {
        super(id);

        if (referenceFrame == null) {
            throw new IllegalArgumentException("Pointer reference frame cannot be null.");
        }

        this.referenceFrame = referenceFrame;
        this.type = type;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public final void throwExceptionForIllegalParentChange(@Nullable final BaseValueHierarchy<?> oldParent) throws SetParentNotAllowed {
        if (this.getParent() == null) return;
        try {
            this.tryInitOrthrowExceptionIllegalReferenceFrame();
        } catch (IllegalStateException e) {
            throw new SetParentNotAllowed(e.getMessage());
        }
    }

    /**
     * Automatically try to initialize the pointer and also check if the pointer is still valid if it was
     * initialized and the tree structure changes again.
     */
    @Override
    final void onParentChangeInParents(@NotNull BaseValueHierarchy<?> affectedNode,
                                       @Nullable final BaseValueHierarchy<?> oldParent) throws SetParentNotAllowed {
        if (affectedNode.getParent() == null) return;
        try {
            this.tryInitOrthrowExceptionIllegalReferenceFrame();
        } catch (IllegalStateException e) {
            throw new SetParentNotAllowed(e.getMessage());
        }
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

    @Override
    public boolean canSet(Object newValue) throws UserException {
        if (!this.initialized) throw new UserException("ValuePointer is not initialized.");
        if (newValue == null) return true;
        return this.type.isAssignableFrom(newValue.getClass());
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
        if (!this.initialized) {
            this.tryInitOrthrowExceptionIllegalReferenceFrame();
            if (!this.initialized) {
                throw new IllegalStateException("ValuePointer is not initialized.");
            }
        }

        if (this.referenced == value) {
            if (value == null) this.cachedUniquePath = null;
            return;
        }

        /* clear pointer */
        if (value == null) {
            final T prev = this.referenced;
            final String prevCachedPath = this.cachedUniquePath;
            this.referenced = null;
            try {
                prev.removePointer(this);
            } catch (Exception e) {
                this.referenced = prev;
                throw e;
            }

            this.cachedUniquePath = null;
            this.triggerEvent(new ValueChangeEvent(this, new StringType(prevCachedPath), new StringType("")));
        } else {
            /* change pointer */
            ValuePath path = value.getRelativePath(this.referenceFrame);
            if (!value.isChildOf(this.referenceFrame)) {
                throw new IllegalArgumentException("Cannot point to a node that is not a child of the pointer's reference frame");
            }

            String uniquePath;
            if (path == null || (uniquePath = path.toUniquePath()).isEmpty()) {
                throw new IllegalArgumentException("Cannot point to a node with an empty unique path.");
            }

            final T prev = this.referenced;
            this.referenced = value;
            try {
                this.referenced.addPointer(this);
            } catch (Exception e) {
                this.referenced = prev;
                throw e;
            }

            if (prev != null) {
                try {
                    prev.removePointer(this);
                } catch (Exception e) {
                    this.referenced = prev;
                    try {
                        value.removePointer(this);
                    } catch (Exception e1) {
                        throw new MemoryLeakError();
                    }
                }
            }

            String prevPath = this.cachedUniquePath;
            this.cachedUniquePath = uniquePath;
            this.triggerEvent(new ValueChangeEvent(this, new StringType(prevPath), new StringType(uniquePath)));
        }
    }

    public T getReference() {
        return this.referenced;
    }

    @Override
    public StringType valueToData() {
        return new StringType(this.cachedUniquePath);
    }

    @Override
    public @Nullable T valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        //TODO serialization is just placeholder, we need to check nulls in serialization and how to deal with them
        StringType value;
        if ((value = data.asStringType()) == null) throw new BaseTypeCannotBeParsed("ValuePointer expects stringtype.");

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

    @Override
    public StringType toData() {
        return this.valueToData();
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