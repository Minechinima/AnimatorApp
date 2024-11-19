package com.minechinima.model.events.types;

import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.Nullable;

public class ParentChangeEvent extends Event {
    @Nullable
    private final BaseStructure oldParent;
    @Nullable
    private final BaseStructure newParent;

    /**
     * @param trigger
     * @param oldParent
     * @param newParent
     * @throws IllegalStateException if both parents are equal and therefore, no change happened.
     */
    public ParentChangeEvent(BaseStructure trigger, @Nullable BaseStructure oldParent, @Nullable BaseStructure newParent) {
        super(trigger);
        this.oldParent = oldParent;
        this.newParent = newParent;
        if (this.oldParent == this.newParent) {
            throw new IllegalStateException("A ParentChange event was triggered where both parents are equal.");
        }
    }

    public BaseStructure getOldParent() {
        return oldParent;
    }

    public BaseStructure getNewParent() {
        return newParent;
    }
}
