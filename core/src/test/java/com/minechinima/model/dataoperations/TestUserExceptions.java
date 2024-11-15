package com.minechinima.model.dataoperations;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.base.DummyValueGroup;
import com.minechinima.model.base.ValuePointer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestUserExceptions {

    @Test
    public void test() {
        DummyValueGroup root = new DummyValueGroup("root");
        DummyValueGroup owner = new DummyValueGroup("test");
        PointerTest test = new PointerTest("ptr", owner, root);
        root.add(owner);
        owner.add(test);
        PointerTest data = new PointerTest("ptr", owner, root);
        data.set(owner);

        ValueChange op = new ValueChange(test, data.valueToData());
        Assertions.assertThrows(UserException.class, () -> op.undo(test));
    }

    public static class PointerTest extends ValuePointer<DummyValueGroup> {
        private DummyValueGroup owner;
        public PointerTest(String id, DummyValueGroup owner, @NotNull BaseValueHierarchy<? extends BaseStructure> relative) {
            super(id, DummyValueGroup.class, relative);
            this.owner = owner;
        }

        @Override
        public boolean canSet(Object newValue) throws UserException {
            if (!(newValue instanceof DummyValueGroup dummy)) {
                return false;
            }

            if (newValue == this.owner) {
                throw new UserException("ERROR");
            }
            return true;
        }
    }
}
