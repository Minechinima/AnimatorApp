package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.exceptions.PathParsingException;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.utils.IPointer;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This is a value that points to another element in the tree, where this node is located in.<br>
 * It acts like a real pointer, if the target element is deleted, this pointer is automatically set to null.<br>
 * The pointer uses unique paths to uniquely address a target independent of possible identifier changes.<br>
 * This value is not going to serialize and store the target itself, it only points to it with a path.
 * @param <T>
 * @implNote This is in base package to override {@link #throwExceptionForIllegalParentChange(BaseValueHierarchy, BaseValueHierarchy)}
 */
//TODO idea to solve issue of not loading the pointer target: make this an own class with final methods. Write own getter that tries to load everytime it is called.
public class ValuePointer<T extends BaseStructure> extends BaseValue<T> implements IPointer<T> {
    /**
     * When deserialized and the pointer target cannot be found, this will be set to the last known path.
     */
    private String cachedUniquePath;
    private final BaseValueHierarchy<? extends BaseStructure> referenceFrame;
    /**
     * A callback that is called before this pointer's value is actually set.
     * <br>
     * This could be used to prevent illegal pointer values and remove an invalid pointer from the hierarchy before
     * it is actually set and recording a change event for an illegal state.
     */
    private final Set<Consumer<T>> callbacksBeforeSet = Collections.newSetFromMap(new IdentityHashMap<>());
    /**
     * The type of this ValuePointer target.
     * @implNote important because generics suck :3
     */
    private final Class<T> type;

    public ValuePointer(String id, Class<T> type, @NotNull BaseValueHierarchy<? extends BaseStructure> referenceFrame) {
        super(id);
        this.throwExceptionForIllegalReferenceFrame(referenceFrame);
        this.referenceFrame = referenceFrame;
        this.type = type;
    }

    /**
     * @throws IllegalStateException with meaningful debug messages.
     * @implNote This is final as it is a core safety mechanism for the integrity of tree management!
     */
    protected final void throwExceptionForIllegalReferenceFrame(BaseValueHierarchy<? extends BaseStructure> frame) {
        if (frame == null) {
            throw new IllegalStateException("Pointer reference frame cannot be null.");
        }
        /* as long as we don't have a parent we cannot check if this is valid. */
        if (this.getParent() == null) return;

        final BaseValueHierarchy<?> thisRoot = this.getRoot();
        /*
         * A pointer can only point to values that are in the same tree as this pointer.
         * Imagine a pointer in an OS trying to point into inaccessible memory...
         * Another important Reason: we want to record all changes to a tree. If a pointer could point to a value in a
         * completely separate tree, we cannot record the change that resulted in clearing this pointer.
         * This would break the core design principle of the data tree that everything is fully revert-able and recordable.
         */
        if (thisRoot != frame.getRoot()) {
            //throw new IllegalStateException("Pointer reference frame is out of reach.");
        }

        /*
         * Next requirement: A reference frame can never be something that could be removed from the tree.
         * How do you want to point to something that is not part of the tree anymore?
         * If frame is root, it is allowed because that means it can never be removed and
         * this pointer will always be a child of the frame, i.e., a closed system.
         */
        if (frame != thisRoot) {
            /* frame is not a root, so this must mean it has a parent. */
            if (frame.getParent() == null) {
                //throw new IllegalStateException("Pointer reference frame has no parent. " +
                //        "Not possible: it is not the root but part of the same tree.");
            }
            /*
             * ValueGroup is the only BaseValueHierarchy implementation guaranteed to have static children.
             * If the frame is not a child of a ValueGroup, it means it could be removed at runtime. If this happens,
             * where should this pointer point to? Also memory leak since this pointer would still hold that removed node.
             * //TODO more general check is needed whether the frame could be removed, go through all parents,
             *    and whether the node where it could be removed
             *    is a parent of this i.e. closed system again. what about if the valuepointer is removed from tree and added in again?
             */
            if (!(frame.getParent() instanceof BaseImmutableValueHierarchy<?>)) {
                /*
                 * If the reference frame could be removed at runtime, it is still okay if this pointer
                 * is a child of that frame, as then it is a closed system.
                 */
                if (!this.isChildOf(frame)) {
                    //TODO throw new IllegalStateException("Pointer reference frame could be removed from the tree at runtime, creating an illegal pointer.");
                }
            }
        }
    }

    @Override
    final void throwExceptionForIllegalParentChange(BaseValueHierarchy<?> oldParent, BaseValueHierarchy<?> newParent) throws SetParentNotAllowed {
        try {
            this.throwExceptionForIllegalReferenceFrame(this.referenceFrame);
        } catch (Exception e) {
            throw new SetParentNotAllowed(e);
        }
    }

    public Class<T> getType() {
        return this.type;
    }

    public void addCallbackBeforeSet(Consumer<T> callback) {
        this.callbacksBeforeSet.add(callback);
    }

    public void removeCallbackBeforeSet(Consumer<T> callback) {
        this.callbacksBeforeSet.remove(callback);
    }

    public BaseValueHierarchy<? extends BaseStructure> getRelative() {
        return this.referenceFrame;
    }

    public String getCachedUniquePath() {
        return this.cachedUniquePath;
    }

    @Override
    public StringType toData() {
        return this.get() != null ? new StringType(this.cachedUniquePath) : new StringType("");
    }

    @Override
    public void fromData(BaseType<?> data) {
        StringType value;
        if ((value = data.asStringType()) == null) return;
        /* first set to null because we are not sure whether the path will lead to a pointer target */
        this.set(null);
        this.cachedUniquePath = value.getValue().isEmpty() ? null : value.getValue();

        if (this.cachedUniquePath != null) {
            //TODO
            BaseStructure target = this.referenceFrame.getChildByUniquePath(this.cachedUniquePath);

            if (target != null && this.type.isAssignableFrom(target.getClass())) {
                this.set((T) target);
            }
        }
    }

    @Override
    public void setNullPointer() {
        this.set(null);
    }

    @Override
    public void point(T target) {
        this.set(target);
    }

    @Override
    public T getReference() {
        return this.get();
    }

    @Override
    public void set(T value) {
        if (this.get() == value) return;
        if (value == null) {
            T prev = this.get();

            try {
                for (Consumer<T> callback : this.callbacksBeforeSet) {
                    callback.accept(null);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
            super.set(null);
            /* pointer successfully cleared - let pointer target know */
            if (this.get() == null) {
                this.cachedUniquePath = null;
                prev.removePointer(this);
            }

            return;
        }

        ValuePath path = value.getRelativePath(this.referenceFrame);
        if (path == null) {
            System.out.println("Cannot set ValuePointer as the path of the value to the relative target is empty.");
            return;
        }

        BaseStructure target = this.referenceFrame.getChildByUniquePath(path.toUniquePath());
        if (target == null || target != value) {
            System.out.println("Cannot set ValuePointer as the pointer target cannot be retrieved from the relative target.");
            return;
        }

        try {
            for (Consumer<T> callback : this.callbacksBeforeSet) {
                callback.accept(value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        T prev = this.get();
        super.set(value);
        if (this.get() != prev) {
            this.cachedUniquePath = path.toUniquePath();
        }
        /* if the pointer really changed, remove from old pointer target */
        if (prev != null && this.get() != prev) {
            prev.removePointer(this);
        }

        this.get().addPointer(this);
    }

    @Override
    public BaseType<?> valueToData() {
        return this.get() != null ? new StringType(this.cachedUniquePath) : new StringType("");
    }

    @Override
    public @Nullable T valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        StringType value;
        if ((value = data.asStringType()) == null) throw new BaseTypeCannotBeParsed("ValuePointer expects stringtype.");

        try {
            BaseStructure target = this.referenceFrame.getChildByUniquePath(new ValuePath(value.getValue()));
            if (target == null) {
                return null;
            } else if (this.type.isAssignableFrom(target.getClass())) {
                return (T) target;
            } else {
                return null;
            }
        } catch (PathParsingException e) {
            throw new BaseTypeCannotBeParsed(e);
        }
    }

    @Override
    public boolean canSet(Object newValue) throws UserException {
        if (newValue == null) return true;
        return this.type.isAssignableFrom(newValue.getClass());
    }
}