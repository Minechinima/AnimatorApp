package com.minechinima.model.base;

import com.minechinima.model.base.dummies.*;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.DummyHistoryManager;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;
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
        assertEquals("Cannot point to a node that is not a child of the pointer's reference frame", e.getMessage());
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
        assertEquals("Cannot point to a node with an empty unique path.", e.getMessage());
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
}
