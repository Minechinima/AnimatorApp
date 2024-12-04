package com.minechinima.model.values;

import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.base.dummies.DummyBaseStructure;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.lists.ValuePointerList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValuePointerListTest {
    @Test
    public void testSetPointerToNull() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        ValuePointerList<DummyBaseStructure> list = new ValuePointerList<>("test", DummyBaseStructure.class, root,
                false, true);
        DummyBaseStructure target = new DummyBaseStructure("target");

        root._addChild("list", list);
        root._addChild("target", target);
        root.initChildren();
        list.addPointer(target);

        list.get(0).set(null);
        assertEquals(0, list.size());
    }

    @Test
    public void testAddNullPointer() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root", ValuePath.Format.PATH);
        ValuePointerList<DummyBaseStructure> list = new ValuePointerList<>("test", DummyBaseStructure.class, root,
                false, true);
        DummyBaseStructure target = new DummyBaseStructure("target");

        root._addChild("list", list);
        root._addChild("target", target);
        root.initChildren();
        Exception e = assertThrows(Exception.class, () -> list.add(new ValuePointer<>("", DummyBaseStructure.class, root)));
        assertEquals("com.minechinima.model.exceptions.SetParentNotAllowed: " +
                "Parent does not allow null pointers.", e.getMessage());
    }
}
