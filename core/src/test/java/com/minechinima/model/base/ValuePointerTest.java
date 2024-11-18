package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import com.minechinima.model.values.FloatValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyMutableIDValue pointerTarget = new DummyMutableIDValue("target");
        root.add(pointerTarget);
        ValuePointer<DummyMutableIDValue> pointer = new ValuePointer<>("", DummyMutableIDValue.class, root);
        pointer.set(pointerTarget);
        pointerTarget.clearNodeFromMemory();
        Assertions.assertNull(pointer.get());
    }
    @Test
    public void testFromDataTargetIDMutable() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyMutableIDValue pointerTarget = new DummyMutableIDValue("target");
        root.add(pointerTarget);
        ValuePointer<DummyMutableIDValue> pointer = new ValuePointer<>("", DummyMutableIDValue.class, root);
        pointer.fromData(new StringType(pointerTarget.getRelativePath(root).toUniquePath()));
        Assertions.assertEquals(pointerTarget, pointer.get());
    }

    @Test
    public void testFromDataTargetNormalIdentifier() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseStructure pointerTarget = new DummyBaseStructure("target");
        root.add(pointerTarget);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, root);
        pointer.fromData(new StringType(pointerTarget.getRelativePath(root).toUniquePath()));
        Assertions.assertEquals(pointerTarget, pointer.get());
    }

    @Test
    public void testSetTargetUnreachable() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseStructure pointerTarget = new DummyBaseStructure("target");
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, root);
        pointer.set(pointerTarget);
        Assertions.assertEquals(null, pointer.get());
    }

    @Test
    public void testCachedPathAfterFromDataUnreachable() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, root);
        pointer.fromData(new StringType("test"));
        Assertions.assertEquals("test", pointer.getCachedUniquePath());
    }
}
