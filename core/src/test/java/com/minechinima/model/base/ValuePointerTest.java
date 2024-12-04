package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.dummies.*;
import com.minechinima.model.exceptions.ContractViolatedException;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.DummyHistoryManager;
import com.minechinima.model.utils.IValuePointerConstraint;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ValuePointerTest {
    @Test
    public void illegalReferenceFrameOutOfReach() {
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer pointer = new ValuePointer("", BaseStructure.class, ref);
        root.add(pointer);
        Exception e = assertThrows(Exception.class, () -> pointer.set(null));
        assertEquals("ValuePointer is not initialized.", e.getMessage());
        assertFalse(pointer.isInitialized());
    }

    @Test
    public void illegalReferenceFrameNull() {
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer[] pointer = new ValuePointer[1];
        Exception e = assertThrowsExactly(IllegalArgumentException.class,
                () -> pointer[0] = new ValuePointer("", BaseStructure.class, null));
        assertEquals("Pointer reference frame cannot be null.", e.getMessage());
        assertNull(pointer[0]);
    }

    @Test
    public void illegalReferenceFrameRemovable() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer pointer = new ValuePointer("", BaseStructure.class, ref);

        root.add(ref);

        Exception e = assertThrows(Exception.class, () -> root.add(pointer));
        assertEquals("com.minechinima.model.exceptions.SetParentNotAllowed: " +
                "The pointer reference frame could be removed from the tree and the pointer is not a child of the frame.", e.getMessage());
        Assertions.assertFalse(pointer.isInitialized());
    }

    @Test
    public void validReferenceFrameRemovableDirectChild() {

        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer pointer = new ValuePointer("", BaseStructure.class, ref);

        root.add(ref.add(pointer));
        Assertions.assertTrue(pointer.isInitialized());
    }

    @Test
    public void validReferenceFrameRemovableDistantChildOfFrame() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy h1 = new DummyBaseImmutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy h2 = new DummyBaseImmutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer pointer = new ValuePointer("", BaseStructure.class, ref);

        root.add(
                ref.add(
                        h2));

        h2._add("pointer", pointer);
        h2.initChildren();

        Assertions.assertTrue(pointer.isInitialized());
    }

    @Test
    public void validReferenceFrameRemovableDistantChildOfImmutable() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy ref = new DummyBaseImmutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy h1 = new DummyBaseImmutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy h2 = new DummyBaseImmutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer pointer = new ValuePointer("", BaseStructure.class, ref);

        root.add(h1);
        h2._add("ref", ref);
        h2.initChildren();

        h1._add("h2", h2);
        h1._add("pointer", pointer);
        h1.initChildren();
        pointer.set(null);
        Assertions.assertTrue(pointer.isInitialized());
    }

    @Test
    public void illegalReferenceFrameAfterPointerParentChange() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer pointer = new ValuePointer("", BaseStructure.class, ref);

        root.add(h2,
                ref.add(h1.add(
                                pointer)));
        assertTrue(pointer.isInitialized());
        h1.children.remove(pointer);
        Exception e = assertThrows(Exception.class, () -> h2.add(pointer));
        assertEquals("com.minechinima.model.exceptions.SetParentNotAllowed: " +
                "The pointer reference frame could be removed from the tree and the pointer is not a child of the frame.",
                e.getMessage());
        assertEquals(h1, pointer.getParent());
        assertTrue(pointer.isInitialized());
    }

    @Test
    public void illegalReferenceFrameAfterDistantParentOfPointerChanged() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer pointer = new ValuePointer("", BaseStructure.class, ref);

        root.add(h2,
                ref.add(
                        h1.add(
                                h3.add(pointer))));

        assertTrue(pointer.isInitialized());
        h1.children.remove(h3);
        Exception e = assertThrows(Exception.class, () -> h2.add(h3));
        assertEquals("com.minechinima.model.exceptions.SetParentNotAllowed: " +
                        "The pointer reference frame could be removed from the tree and the pointer is not a child of the frame.",
                e.getMessage());
        assertEquals(h3, pointer.getParent());
        assertTrue(pointer.isInitialized());
    }

    @Test
    public void setValid() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, ref);
        DummyBaseStructure point = new DummyBaseStructure("a");

        root.add(ref.add(pointer, point));

        pointer.set(point);
        assertEquals(point, pointer.getReference());
        assertEquals("a", pointer.getCachedUniquePath());
    }

    @Test
    public void setToOutOfReach() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, ref);
        DummyBaseStructure point = new DummyBaseStructure("a");

        root.add(ref.add(pointer), point);

        Exception e = assertThrowsExactly(IllegalArgumentException.class, () -> pointer.set(point));
        assertEquals("Cannot point to a node that is not a child of the pointer's reference frame.", e.getMessage());
        assertNull(pointer.getReference());
    }

    @Test
    public void setNotInitialized() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, ref);
        DummyBaseStructure point = new DummyBaseStructure("a");

        root.add(pointer, point);

        Exception e = assertThrowsExactly(IllegalStateException.class, () -> pointer.set(point));
        assertEquals("ValuePointer is not initialized.", e.getMessage());
        assertNull(pointer.getReference());
    }

    @Test
    public void setElementWithEmptyPath() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, ref);
        DummyBaseStructure point = new DummyBaseStructure("");

        root.add(ref.add(pointer, point));

        Exception e = assertThrowsExactly(IllegalArgumentException.class, () -> pointer.set(point));
        assertEquals("Cannot point to a node as it is not addressable with a path.", e.getMessage());
        assertNull(pointer.getReference());
    }

    @Test
    public void setNonNullToNull() throws NoSuchFieldException, IllegalAccessException {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, ref);
        DummyBaseStructure point = new DummyBaseStructure("a");

        root.add(ref.add(pointer, point));

        testSetPointerSuccess(pointer, point);

        pointer.setNullPointer();
        Assertions.assertNull(pointer.getReference());
        Assertions.assertEquals(null, pointer.getCachedUniquePath());
    }

    @Test
    public void setNonNullToNonNull() throws NoSuchFieldException, IllegalAccessException {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, ref);
        DummyBaseStructure point = new DummyBaseStructure("a");
        DummyBaseStructure point2 = new DummyBaseStructure("b");

        root.add(ref.add(pointer, point, point2));

        testSetPointerSuccess(pointer, point);
        testSetPointerSuccess(pointer, point2);
    }

    private static <T extends BaseStructure> void testSetPointerSuccess(ValuePointer<T> pointer, T newReference) throws NoSuchFieldException, IllegalAccessException {
        T prev = pointer.getReference();
        pointer.set(newReference);
        Assertions.assertEquals(newReference, pointer.getReference());

        Field pointersToThisField = BaseStructure.class.getDeclaredField("pointersToThis");
        pointersToThisField.setAccessible(true);

        if (newReference != null) {
            Set<ValuePointer<?>> pointersToThis = (Set<ValuePointer<?>>) pointersToThisField.get(newReference);
            Assertions.assertTrue(pointersToThis.contains(pointer));
        }

        if (prev != null) {
            Set<ValuePointer<?>> pointersToThisPrev = (Set<ValuePointer<?>>) pointersToThisField.get(prev);
            Assertions.assertFalse(pointersToThisPrev.contains(pointer));
        }
    }

    @Test
    public void clearPointerOnReferenceRemove() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy ref = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        ValuePointer<DummyBaseStructure> pointer = new ValuePointer<>("", DummyBaseStructure.class, ref);
        DummyBaseStructure point = new DummyBaseStructure("a");

        root.add(ref.add(pointer, point));

        pointer.set(point);

        ref.removeChild(point);
        Assertions.assertNull(pointer.getReference());
    }

    @Test
    public void undoRemoveNodeAndPointerClear() throws UserException {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("", ValuePath.Format.PATH);
        DummyDoubleValueMap map = new DummyDoubleValueMap("map");
        ValuePointer<DoubleValue> pointer = new ValuePointer<>("ptr", DoubleValue.class, map);
        DoubleValue point = new DoubleValue("a");

        root._addChild("ref", map);
        root._addChild("ptr", pointer);
        root.initChildren();
        map.add(point);
        pointer.set(point);
        point.set(2D);

        DummyHistoryManager historyManager = new DummyHistoryManager(root);
        root.getLogging().registerLog(historyManager);

        map.removeChild(point);
        Assertions.assertNull(pointer.getReference());
        historyManager.commitUndoStep();

        historyManager.undo();
        Assertions.assertNotNull(map.get("a"));
        Assertions.assertEquals(2D, pointer.getReference().get());
    }

    @Test
    public void testEmptyTargetPath() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("test", ValuePath.Format.PATH);
        DummyBaseStructure target = new DummyBaseStructure("");
        ValuePointer<DummyBaseStructure> ptr = new ValuePointer<>("", DummyBaseStructure.class, root);

        root._addChild("h1", h1);
        root._addChild("ptr", ptr);
        root.initChildren();
        h1.add(target);

        Exception e = assertThrows(Exception.class, () -> ptr.set(target));
        assertEquals("Cannot point to a node as it is not addressable with a path.", e.getMessage());
    }

    @Test
    public void setToDuplicateOnConstrained() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        DummyBaseStructure target = new DummyBaseStructure("test");
        ValuePointer<DummyBaseStructure> ptr1 = new ValuePointer<>("1", DummyBaseStructure.class, root);
        ValuePointer<DummyBaseStructure> ptr = new ValuePointer<>("2", DummyBaseStructure.class, root);
        DummyConstraintedHierarchy<DummyBaseStructure> map = new DummyConstraintedHierarchy<>("", DummyBaseStructure.class, root);
        map.duplicateAllowed = false;

        root._addChild("target", target);
        root._addChild("map", map);
        root.initChildren();
        map.add(ptr1);
        map.add(ptr);

        ptr1.set(target);

        Exception e = assertThrows(Exception.class, () -> ptr.set(target));
        Assertions.assertEquals("Parent does not allow duplicate pointer targets.", e.getMessage());
        Assertions.assertTrue(map.contains(ptr));
        Assertions.assertNull(ptr.getReference());
    }

    @Test
    public void setToDuplicateOnConstrainedRemoveOnCallback() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        DummyBaseStructure target = new DummyBaseStructure("test");
        ValuePointer<DummyBaseStructure> ptr1 = new ValuePointer<>("1", DummyBaseStructure.class, root);
        ValuePointer<DummyBaseStructure> ptr = new ValuePointer<>("2", DummyBaseStructure.class, root);
        DummyConstraintedHierarchy<DummyBaseStructure> map = new DummyConstraintedHierarchy<>("", DummyBaseStructure.class, root);
        map.duplicateAllowed = false;
        map.deleteWhenDuplicate = true;

        root._addChild("target", target);
        root._addChild("map", map);
        root.initChildren();
        map.add(ptr1);
        map.add(ptr);

        ptr1.set(target);
        ptr.set(target);

        Assertions.assertFalse(map.contains(ptr));
        Assertions.assertEquals(target, ptr.getReference());
    }

    @Test
    public void setNonNullToNullOnConstrained() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        DummyBaseStructure target = new DummyBaseStructure("test");
        ValuePointer<DummyBaseStructure> ptr = new ValuePointer<>("", DummyBaseStructure.class, root);
        DummyConstraintedHierarchy<DummyBaseStructure> map = new DummyConstraintedHierarchy<>("", DummyBaseStructure.class, root);
        map.nullAllowed = false;

        root._addChild("target", target);
        root._addChild("map", map);
        root.initChildren();

        ptr.set(target);
        map.add(ptr);
        Exception e = assertThrows(Exception.class, () -> ptr.set(null));
        Assertions.assertEquals("Parent does not allow null pointers.", e.getMessage());
        Assertions.assertTrue(map.contains(ptr));
        Assertions.assertEquals(target, ptr.getReference());
    }

    @Test
    public void setNonNullToNullOnConstrainedRemoveOnCallback() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        DummyBaseStructure target = new DummyBaseStructure("test");
        ValuePointer<DummyBaseStructure> ptr = new ValuePointer<>("", DummyBaseStructure.class, root);
        DummyConstraintedHierarchy<DummyBaseStructure> map = new DummyConstraintedHierarchy<>("", DummyBaseStructure.class, root);
        map.nullAllowed = false;
        map.deleteWhenNull = true;

        root._addChild("target", target);
        root._addChild("map", map);
        root.initChildren();

        ptr.set(target);
        map.add(ptr);
        ptr.set(null);

        Assertions.assertFalse(map.contains(ptr));
        Assertions.assertNull(ptr.getReference());
    }

    @Test
    public void undoSetNonNullToNullOnConstrainedRemoveOnCallback() throws UserException {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        DummyBaseStructure target = new DummyBaseStructure("test");
        ValuePointer<DummyBaseStructure> ptr = new ValuePointer<>("ptr", DummyBaseStructure.class, root);
        DummyConstraintedHierarchy<DummyBaseStructure> map = new DummyConstraintedHierarchy<>("map", DummyBaseStructure.class, root);
        map.nullAllowed = false;
        map.deleteWhenNull = true;

        root._addChild("target", target);
        root._addChild("map", map);
        root.initChildren();

        ptr.set(target);
        map.add(ptr);

        DummyHistoryManager historyManager = new DummyHistoryManager(root);
        root.getLogging().registerLog(historyManager);

        ptr.set(null);

        historyManager.commitUndoStep();
        Assertions.assertFalse(map.contains(ptr));
        Assertions.assertNull(ptr.getReference());

        historyManager.undo();
        Assertions.assertNotNull(map.get("ptr"));
        Assertions.assertEquals(target, map.get("ptr").getReference());
    }

    @Test
    public void addNullPointerToConstrained() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        DummyBaseStructure target = new DummyBaseStructure("");
        ValuePointer<DummyBaseStructure> ptr = new ValuePointer<>("", DummyBaseStructure.class, root);
        DummyConstraintedHierarchy<DummyBaseStructure> map = new DummyConstraintedHierarchy<>("", DummyBaseStructure.class, root);
        map.nullAllowed = false;

        root._addChild("target", target);
        root._addChild("map", map);
        root.initChildren();

        Exception e = assertThrows(Exception.class, () -> map.add(ptr));
        Assertions.assertEquals("Parent does not allow null pointers.", e.getCause().getMessage());
        Assertions.assertFalse(map.contains(ptr));
    }

    @Test
    public void addDuplicatePointerTargetToConstrained() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        DummyBaseStructure target = new DummyBaseStructure("test");
        ValuePointer<DummyBaseStructure> ptr1 = new ValuePointer<>("1", DummyBaseStructure.class, root);
        ValuePointer<DummyBaseStructure> ptr = new ValuePointer<>("2", DummyBaseStructure.class, root);
        DummyConstraintedHierarchy<DummyBaseStructure> map = new DummyConstraintedHierarchy<>("", DummyBaseStructure.class, root);
        map.duplicateAllowed = false;

        root._addChild("target", target);
        root._addChild("map", map);
        root.initChildren();
        map.add(ptr1);
        ptr1.set(target);
        ptr.set(target);

        Exception e = assertThrows(Exception.class, () -> map.add(ptr));
        Assertions.assertEquals("Parent does not allow duplicate pointer targets.", e.getCause().getMessage());
        Assertions.assertFalse(map.contains(ptr));
    }

    public static class DummyConstraintedHierarchy<T extends BaseStructure> extends BaseValueMap<ValuePointer<T>> implements IValuePointerConstraint {
        public boolean nullAllowed = true;
        public boolean duplicateAllowed = true;
        public boolean deleteWhenNull = false;
        public boolean deleteWhenDuplicate = false;
        private final BaseValueHierarchy<?> referenceFrame;
        private final Class<T> type;

        public DummyConstraintedHierarchy(String id, Class<T> type, BaseValueHierarchy<?> referenceFrame) {
            super(id);
            this.type = type;
            this.referenceFrame = referenceFrame;
        }

        @Override
        public boolean nullPointerAllowed() {
            return nullAllowed;
        }

        @Override
        public boolean duplicatePointerTargetsAllowed() {
            return duplicateAllowed;
        }

        public void callbackOnNullPointer(ValuePointer pointer) {
            if (this.deleteWhenNull) {
                this.remove(pointer);
            }
        }

        public void callbackOnDuplicatePointerTarget(ValuePointer pointer) {
            if (this.deleteWhenDuplicate) {
                this.remove(pointer);
            }
        }

        @Override
        protected @NotNull ValuePointer<T> instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
            ValuePointer<T> ptr = new ValuePointer<>(id, this.type, this.referenceFrame);
            ptr.fromData(data);
            return ptr;
        }
    }
}
