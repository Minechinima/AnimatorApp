package com.minechinima.model.utils;

/**
 * Interface for an observer pattern for a pointer like behavior when objects are supposed to be cleared from memory.
 * @param <T>
 */
public interface IPointer<T extends IPointerTarget> {
    /**
     * Remove the reference of this pointer, setting the target to null.
     * <br><br>
     * This needs to call {@link IPointerTarget#removePointer(IPointer)} if this pointer is successfully cleared.
     */
    default void setNullPointer() {
        /* example implementation */
        T target = this.getReference();
        this.point(null);
        /* pointer target successfully removed */
        if (target != null && this.getReference() == null) {
            target.removePointer(this);
        }
    }

    /**
     * Set this pointer's reference.
     * <br><br>
     * This needs to call {@link IPointerTarget#addPointer(IPointer)} if a pointer is successfully set and
     * {@link IPointerTarget#removePointer(IPointer)} when the target changed.
     * @param target
     */
    void point(T target);

    /**
     * @return the object this pointer references.
     */
    T getReference();
}
