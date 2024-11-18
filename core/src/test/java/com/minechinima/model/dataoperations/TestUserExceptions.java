package com.minechinima.model.dataoperations;

import com.minechinima.model.base.*;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestUserExceptions {

    @Test
    public void test() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy owner = new DummyBaseValueHierarchy("test", ValuePath.Format.PATH);
        PointerTest test = new PointerTest("ptr", owner, root);
        root.add(owner);
        owner.add(test);
        PointerTest data = new PointerTest("ptr", owner, root);
        data.set(owner);

        ValueChange op = new ValueChange(test, data.valueToData());
        Assertions.assertThrows(UserException.class, () -> op.undo(test));
    }

    public static class PointerTest extends ValuePointer<DummyBaseValueHierarchy> {
        private DummyBaseValueHierarchy owner;
        public PointerTest(String id, DummyBaseValueHierarchy owner, @NotNull BaseValueHierarchy<? extends BaseStructure> relative) {
            super(id, DummyBaseValueHierarchy.class, relative);
            this.owner = owner;
        }

        @Override
        public boolean canSet(Object newValue) throws UserException {
            if (!(newValue instanceof DummyBaseValueHierarchy dummy)) {
                return false;
            }

            if (newValue == this.owner) {
                throw new UserException("ERROR");
            }
            return true;
        }
    }
}
