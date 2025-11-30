package com.minechinima.model.app.object;

import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.exceptions.UserException;
import org.jetbrains.annotations.NotNull;

public class ObjectParentPointer extends ValuePointer<BaseObject> {
    private final BaseObject owner;
    public ObjectParentPointer(String id, @NotNull BaseObject owner, @NotNull BaseValueHierarchy<?> relative) {
        super(id, BaseObject.class, relative);
        this.owner = owner;
    }

    @Override
    public boolean canSet(Object newValue) throws UserException {
        if (!(newValue instanceof BaseObject newParent)) return false;

        if (newParent == this.owner) {
            throw new UserException("Cannot set parent to the same object.");
        }

        if (newParent != null) {
            for (BaseObject p = newParent.parent.get(); p != null; p = p.parent.get()) {
                if (p == this.owner) {
                    throw new UserException("Loop in parents.");
                }
            }
        }

        return true;
    }
}
