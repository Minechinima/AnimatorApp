package com.minechinima.model.base;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.dataoperations.datainterfaces.ListElement;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import com.minechinima.model.values.FloatValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ValuePointerTest {
    @Test
    public void getValueByPathWrongType() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("test2", ValuePath.Format.PATH);
        ValuePointer<DoubleValue> pointer = new ValuePointer<>("test", DoubleValue.class, root);
        DoubleValue target = new DoubleValue("target");

        root.add(h1);
        h1.add(pointer);
        root.add(target);
        pointer.set(target);
        BaseType<?> data = pointer.toData();
        pointer.set(null);
        root.remove(target);
        root.add(new FloatValue("target"));
        pointer.fromData(data);
        DoubleValue test = pointer.get();
        Assertions.assertNull(test);
    }

    @Test
    public void testPointerClearedAfterRemovedFromTree() {
        ValueGroup root = new ValueGroup("root") {};
        DummyMutableIDValue pointerTarget = new DummyMutableIDValue("target");
        root.add(pointerTarget);
        ValuePointer<DummyMutableIDValue> pointer = new ValuePointer<>("", DummyMutableIDValue.class, root);
        pointer.set(pointerTarget);
        pointerTarget.clearNodeFromMemory();
        Assertions.assertNull(pointer.get());
    }
    @Test
    public void testFromDataTargetIDMutable() {
        ValueGroup root = new ValueGroup("root") {};
        DummyMutableIDValue pointerTarget = new DummyMutableIDValue("target");
        root.add(pointerTarget);
        ValuePointer<DummyMutableIDValue> pointer = new ValuePointer<>("", DummyMutableIDValue.class, root);
        pointer.fromData(new StringType(pointerTarget.getRelativePath(root).toUniquePath()));
        Assertions.assertEquals(pointerTarget, pointer.get());
    }

    @Test
    public void testFromDataTargetNormalIdentifier() {
        ValueGroup root = new ValueGroup("root") {};
        DummyBaseStructure pointerTarget = new DummyBaseStructure("target");
        root.add(pointerTarget);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, root);
        pointer.fromData(new StringType(pointerTarget.getRelativePath(root).toUniquePath()));
        Assertions.assertEquals(pointerTarget, pointer.get());
    }

    @Test
    public void testSetTargetUnreachable() {
        ValueGroup root = new ValueGroup("root") {};
        DummyBaseStructure pointerTarget = new DummyBaseStructure("target");
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, root);
        pointer.set(pointerTarget);
        Assertions.assertEquals(null, pointer.get());
    }

    @Test
    public void testCachedPathAfterFromDataUnreachable() {
        ValueGroup root = new ValueGroup("root") {};
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, root);
        pointer.fromData(new StringType("test"));
        Assertions.assertEquals("test", pointer.getCachedUniquePath());
    }

    @Test
    public void testPointerWaitRelativeFromData() {
        boolean[] fromDataCalled = new boolean[]{false};
        ValueGroup root = new ValueGroup("root") {};
        BaseValueList<DoubleValue> pointerTargetOwner = new BaseValueList<>("list") {
            @Override
            public boolean canAdd(int index, ListElement<Object> element) throws UserException {
                return true;
            }

            @Override
            protected DoubleValue instantiate(String id, UUID uuid, BaseType<?> data1) {
                DoubleValue v = new DoubleValue(id) {
                    @Override
                    public void fromData(BaseType<?> data) {
                        super.fromData(data);
                        fromDataCalled[0] = true;
                    }
                };
                if (data1 != null) v.fromData(data1);
                return v;
            }
        };
        DoubleValue pointerTarget = new DoubleValue("", 123D) {
            @Override
            public void fromData(BaseType<?> data) {
                super.fromData(data);
                fromDataCalled[0] = true;
            }
        };
        pointerTargetOwner.add(pointerTarget);

        ValueGroup pointerOwner = new ValueGroup("owner") {
            @Override
            protected void _fromData(BaseType<?> data) {
                super._fromData(data);

                /* pointerOwner needs to be deserialised before the pointerTarget,
                so the pointerTarget is not available yet for the ValuePointer */
                if (fromDataCalled[0]) Assertions.fail();
            }
        };
        ValuePointer<DoubleValue> pointer = new ValuePointer<>("", DoubleValue.class, root);
        root.add(pointerOwner, pointerTargetOwner);
        pointerOwner.add(pointer);
        pointer.set(pointerTargetOwner.getChildren().get(0));

        BaseType<?> data = root.toData();
        pointerTargetOwner.clear();
        pointer.set(null);

        Assertions.assertEquals(null, pointer.get());
        Assertions.assertEquals(0, pointerTargetOwner.size());

        root.fromData(data);
        Assertions.assertNotNull(pointer.get());
        Assertions.assertEquals(123, pointer.get().get());
    }
}
