package com.minechinima.model.base;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.dataoperations.datainterfaces.ListElement;
import com.minechinima.model.events.*;
import com.minechinima.model.events.changes.ChangeEvent;
import com.minechinima.model.events.changes.IdentifierChangeEvent;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class BaseStructureTest {
    public static Stream<Arguments> provideReferenceManagementTests() {
        return Stream.of(
                Arguments.of(new ReferenceManagementBaseStructure(new DummyBaseStructure(UUID.randomUUID().toString()))
                ));
    }

    private void testSetParent(BaseStructure child, BaseValueHierarchy parent, boolean success) {
        this.testSetParent(child, parent, success, null);
    }

    private void testSetParent(BaseStructure child, BaseValueHierarchy parent, boolean success, @Nullable String message) {
        if (success) {
            try {
                child.setParent(parent);
            } catch (SetParentNotAllowed e) {
                e.printStackTrace();
                Assertions.fail();
            }
        }
        else {
            try {
                child.setParent(parent);
                Assertions.fail();
            } catch (SetParentNotAllowed e) {
                if (message != null) {
                    Assertions.assertEquals(message, e.getMessage());
                }
            }
        }
        Assertions.assertEquals(success, child.getParent() != null);
    }

    @Test
    public void setParent() {
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("parent", ValuePath.Format.PATH);
        BaseStructure value = new DummyBaseStructure("value");

        parent.children.add(value);
        this.testSetParent(value, parent, true);
        Assertions.assertNotNull(parent.getChildByValuePath("value"));
    }

    /**
     * Test setting the parent of a BaseStructure where children wouldn't adhere to the new pathformat of the parent.
     * @param format
     * @param invalids
     */
    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#provideInvalidPathFormatsExamples")
    public void setParentWithInvalidPathFormat(ValuePath.Format format, List<String> invalids) {
        BaseValueHierarchy parent = new DummyBaseValueHierarchy("parent", format);
        for (String invalid : invalids) {
            BaseStructure value = new DummyBaseStructure(invalid);
            this.testSetParent(value, parent, false);
        }
    }

    /**
     * Test setting the parent of a BaseStructure that has children that would have path collisions with children of
     * the new parent.
     * @param format
     */
    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#providePathFormatsWithConcat")
    public void setParentWithChildCollisions(ValuePath.Format format) {
        DummyBaseValueHierarchy root0 = new DummyBaseValueHierarchy("", format);
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("", format);
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("", format);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("", format);
        DummyBaseValueHierarchy h2 = new DummyBaseValueHierarchy("", format);
        DummyBaseValueHierarchy h3 = new DummyBaseValueHierarchy("", format);
        DummyBaseValueHierarchy h4 = new DummyBaseValueHierarchy("", format);

        root0.add(
                root.add(
                        parent.add(
                                h1.add(
                                        new DummyBaseStructure("duplicate"),
                                        new DummyBaseStructure("value")
                                ))));

        h2.add(
                new DummyBaseStructure("test"),
                h3.add(
                        h4.add(
                                new DummyMutableIDValue("duplicate")
                        )));

        parent.children.add(h2);
        this.testSetParent(h2, parent, false, "Cannot set parent as children will have path collisions.");
    }

    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#providePathFormatsWithConcat")
    public void setParentWithCollisionToChangeThis(ValuePath.Format format) {
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("", format);
        DummyBaseStructure h1 = new DummyBaseStructure("duplicate");
        DummyBaseStructure h2 = new DummyBaseStructure("duplicate");

        parent.add(h1);

        parent.children.add(h2);
        this.testSetParent(h2, parent, false, "Can't change the id to resolve a path collision of a" +
                " BaseStructure that does not implement IDMutable");
        parent.children.remove(h2);

        DummyMutableIDValue mutable = new DummyMutableIDValue("duplicate");

        parent.children.add(mutable);
        this.testSetParent(mutable, parent, true);
        Assertions.assertEquals("duplicate.001", mutable.getIdentifier());
        Assertions.assertTrue(parent.getChildren().contains(mutable));
    }

    @Test
    public void setParentNull() {
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseStructure h1 = new DummyBaseStructure("value");

        try {
            h1.setParent(null);
            parent.children.add(h1);
            h1.setParent(parent);
            parent.children.remove(h1);
            h1.setParent(null);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void setParentRecursion() {
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("value", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h2 = new DummyBaseValueHierarchy("value2", ValuePath.Format.PATH);

        parent.children.add(h1);
        h1.children.add(h2);
        /* h2 -> h1 -> parent */
        try {
            h1.setParent(parent);
            h2.setParent(h1);
        } catch (Exception e) {
            Assertions.fail();
        }

        /*
         * Try to create
         * parent -> h2 -> h2 -> parent
         */
        try {
            parent.setParent(h2);
            Assertions.fail();
        } catch (SetParentNotAllowed e) {
            Assertions.assertEquals("Detected loop in parent tree. Can't set parent as it results in recursion.", e.getMessage());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void setParentWhileStillChildOfOld() {
        DummyBaseValueHierarchy parent1 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy parent2 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy v = new DummyBaseValueHierarchy("value", ValuePath.Format.PATH);

        parent1.add(v);

        try {
            v.setParent(parent2);
            Assertions.fail();
        } catch (SetParentNotAllowed e) {
            Assertions.assertEquals("Setting the parent of a BaseStructure to another element," +
                    "while it is still contained in the children of the previous parent, is prohibited to ensure tree integrity.",
                    e.getMessage());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void setParentWhileNotChildOfNew() {
        DummyBaseValueHierarchy parent2 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy v = new DummyBaseValueHierarchy("value", ValuePath.Format.PATH);

        try {
            v.setParent(parent2);
            Assertions.fail();
        } catch (SetParentNotAllowed e) {
            Assertions.assertEquals("The new parent needs to have this in its children so the setParent() method can fully simulate" +
                    "a new tree state and check if setting the parent is allowed.", e.getMessage());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void isChildOf() {
        DummyBaseStructure dummy = new DummyBaseStructure("");
        DummyBaseValueHierarchy h0 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        h0.add(h1);
        h1.add(dummy);

        Assertions.assertTrue(dummy.isChildOf(h1));
        Assertions.assertTrue(dummy.isChildOf(h0));
        Assertions.assertFalse(dummy.isChildOf(new DummyBaseValueHierarchy("", ValuePath.Format.PATH)));
    }

    @Test
    public void getFullPath() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h2 = new DummyBaseValueHierarchy("parent", ValuePath.Format.MAP);
        BaseStructure value = new DummyBaseStructure("value");

        root.add(h1);
        h1.add(h2);
        h2.add(value);

        Assertions.assertEquals("root.parent[\"value\"]", value.getFullPath().toValuePath());
        Assertions.assertEquals("root.parent[\"value\"]", value.getFullPath().toUniquePath());

        Assertions.assertEquals(value.getRelativePath(null).toValuePath(), value.getFullPath().toValuePath());
    }

    @Test
    public void getRelativePathNull() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("parent", ValuePath.Format.PATH);
        BaseStructure value = new DummyBaseStructure("value");

        root.add(h1);
        h1.add(value);

        Assertions.assertEquals("root.parent.value", value.getRelativePath(null).toValuePath());
        Assertions.assertEquals("root.parent.value", value.getRelativePath(null).toUniquePath());
    }

    @Test
    public void getRelativePathWithEmpties() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("parent", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h2 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy value = new DummyBaseValueHierarchy("value", ValuePath.Format.PATH);

        root.add(h1.add(h2.add(value)));

        Assertions.assertEquals("parent.value", value.getRelativePath(null).toValuePath());
        Assertions.assertEquals("parent.value", value.getRelativePath(root).toValuePath());
        Assertions.assertEquals("value", value.getRelativePath(h1).toValuePath());
    }

    @Test
    public void getRelativePathThis() {
        DummyBaseValueHierarchy value = new DummyBaseValueHierarchy("value", ValuePath.Format.PATH);
        Assertions.assertNull(value.getRelativePath(value));
    }

    @Test
    public void getRelativePathNotFound() {
        DummyBaseValueHierarchy value = new DummyBaseValueHierarchy("value", ValuePath.Format.PATH);
        Assertions.assertNull(value.getRelativePath(new DummyBaseValueHierarchy("test", ValuePath.Format.PATH)));
    }

    @ParameterizedTest
    @MethodSource("provideReferenceManagementTests")
    public void testReferenceManagement(ReferenceManagementBaseStructure<? extends BaseStructure> test) throws NoSuchFieldException, IllegalAccessException {
        for (BaseStructure reference : test.assignReferences()) {
            reference.clearNodeFromMemory();
            Assertions.assertFalse(test.hasReference(test.getReferencee(), reference));
            Assertions.assertFalse(test.hasReference(reference, test.getReferencee()));
        }

        List<BaseStructure> references = test.assignReferences();

        test.getReferencee().clearNodeFromMemory();
        for (BaseStructure reference : references) {
            Assertions.assertFalse(test.hasReference(reference, test.getReferencee()));
            Assertions.assertFalse(test.hasReference(test.getReferencee(), reference));
        }
    }

    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#providePathFormats")
    public void pathCollisions(ValuePath.Format format) {
        String nameToTest = "test";

        if (!format.isValidStringForEnclosure(nameToTest)) return;

        String test1Path = "root.test.001";
        DummyMutableIDValue test1 = new DummyMutableIDValue("test");
        String test2Path = "root.test.004";
        DummyMutableIDValue test2 = new DummyMutableIDValue("test");
        String test3Path = "root.test.005";
        DummyMutableIDValue test3 = new DummyMutableIDValue("test");
        String test4Path = "test";
        DummyMutableIDValue test4 = new DummyMutableIDValue("test");
        String test5Path = "test.001";
        DummyMutableIDValue test5 = new DummyMutableIDValue("test.001");
        String test6Path = "root.path.test.001";
        DummyMutableIDValue test6 = new DummyMutableIDValue("test.001");
        String test7Path = "root.path.test";
        DummyMutableIDValue test7 = new DummyMutableIDValue("test");

        String formatPath = "root.format" + format.getStart();
        String test8Path = formatPath + "test" + (format.getMode() == ValuePath.Format.Mode.ENCLOSE ? format.getEnd() : "");
        DummyMutableIDValue test8 = new DummyMutableIDValue("test");
        String test9Path = formatPath + "test.001" + (format.getMode() == ValuePath.Format.Mode.ENCLOSE ? format.getEnd() : "");
        DummyMutableIDValue test9 = new DummyMutableIDValue("test.001");
        String test10Path = formatPath + "test.003" + (format.getMode() == ValuePath.Format.Mode.ENCLOSE ? format.getEnd() : "");
        DummyMutableIDValue test10 = new DummyMutableIDValue("test.003");
        String test11Path = formatPath + "test.002" + (format.getMode() == ValuePath.Format.Mode.ENCLOSE ? format.getEnd() : "");
        DummyMutableIDValue test11 = new DummyMutableIDValue("test");

        String empty1TestPath = "root.format" + (format.getMode() == ValuePath.Format.Mode.ENCLOSE ? format.getStart() + format.getEnd() : "");
        DummyMutableIDValue empty1Test = new DummyMutableIDValue("");
        String empty2TestPath = formatPath + ".001" + (format.getMode() == ValuePath.Format.Mode.ENCLOSE ? format.getEnd() : "");
        DummyMutableIDValue empty2Test = new DummyMutableIDValue("");
        String empty3TestPath = formatPath + ".002" + (format.getMode() == ValuePath.Format.Mode.ENCLOSE ? format.getEnd() : "");
        DummyMutableIDValue empty3Test = new DummyMutableIDValue(".001");

        DummyBaseValueHierarchy rootEmpty = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy path0 = new DummyBaseValueHierarchy("path", ValuePath.Format.PATH);
        DummyBaseValueHierarchy format0 = new DummyBaseValueHierarchy("format", format);
        DummyBaseValueHierarchy empty0 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy empty1 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy empty2 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy empty3 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy empty4 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);

        rootEmpty.add(root);
        root.add(empty0, empty1, path0, format0);
        empty0.add(empty2, empty3);
        empty1.add(empty4);
        empty4.add(new DummyBaseStructure("test"),
                new DummyBaseStructure("test.002"),
                new DummyBaseStructure("test.003"),
                test1, test2);

        /* test where parent is root and empty */
        rootEmpty.add(test4, test5);

        /* test where parent has distant children with path collisions to the added element */
        root.add(test3);

        /* test whether the second element gets a suffix although not needed */
        path0.add(test6, test7);

        format0.add(test8, test9, test10, test11, empty1Test, empty2Test, empty3Test);

        /* this shouldn't change the ids */
        test1.setID("test");
        test2.setID("test");
        test3.setID("test");
        test4.setID("test");
        test5.setID("test");
        test6.setID("test");
        test7.setID("test");
        test8.setID("test");
        test9.setID("test");
        test10.setID("test");
        test11.setID("test");

        Assertions.assertEquals(test1Path, test1.getFullPath().toValuePath());
        Assertions.assertEquals(test2Path, test2.getFullPath().toValuePath());
        Assertions.assertEquals(test3Path, test3.getFullPath().toValuePath());
        Assertions.assertEquals(test4Path, test4.getFullPath().toValuePath());
        Assertions.assertEquals(test5Path, test5.getFullPath().toValuePath());
        Assertions.assertEquals(test6Path, test6.getFullPath().toValuePath());
        Assertions.assertEquals(test7Path, test7.getFullPath().toValuePath());
        Assertions.assertEquals(test8Path, test8.getFullPath().toValuePath());
        Assertions.assertEquals(test9Path, test9.getFullPath().toValuePath());
        Assertions.assertEquals(test10Path, test10.getFullPath().toValuePath());
        Assertions.assertEquals(test11Path, test11.getFullPath().toValuePath());

        if (format.getMode() != ValuePath.Format.Mode.CONCAT) {
            Assertions.assertEquals(empty1TestPath, empty1Test.getFullPath().toValuePath());
            Assertions.assertEquals(empty2TestPath, empty2Test.getFullPath().toValuePath());
            Assertions.assertEquals(empty3TestPath, empty3Test.getFullPath().toValuePath());
        }
    }

    @Test
    public void _setID() {
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("test", ValuePath.Format.MAP);
        DummyMutableIDValue v = new DummyMutableIDValue("a");

        parent.add(v);

        v._setID("test");
        Assertions.assertEquals("test", v.getIdentifier());

        v._setID("");
        Assertions.assertEquals("", v.getIdentifier());

        BaseValueList<DummyMutableIDValue> list = new BaseValueList<DummyMutableIDValue>("") {
            @Override
            public boolean canAdd(int index, ListElement<Object> element) throws UserException {
                return true;
            }

            @Override
            protected @NotNull DummyMutableIDValue instantiate(String id, UUID uuid, BaseType<?> data) {return null;}
        };
        DummyMutableIDValue v2 = new DummyMutableIDValue("a");
        list.add(v2);

        v2._setID("test");
        v2._setID("");
    }

    @Test
    public void _setIDNotAllowed() {
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("test", ValuePath.Format.PATH);
        DummyBaseStructure v = new DummyBaseStructure("a");
        DummyMutableIDValue mutable = new DummyMutableIDValue("b");

        parent.add(v, mutable);

        Assertions.assertThrows(RuntimeException.class, () -> v._setID("test"));

        Assertions.assertThrows(RuntimeException.class, () -> mutable._setID(""));

        mutable.uuidTest = UUID.randomUUID();
        Assertions.assertThrows(RuntimeException.class, () -> mutable._setID("test"));
    }

    @Test
    public void _setIDCollisions() {
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyMutableIDValue mutable = new DummyMutableIDValue("b");
        DummyMutableIDValue mutable2 = new DummyMutableIDValue("c");

        parent.add(new DummyBaseStructure("test"),
                new DummyBaseStructure("test.001"),
                h1.add(new DummyBaseStructure("test.002"), mutable2),
                mutable);

        mutable._setID("test");
        Assertions.assertEquals("test.003", mutable.getIdentifier());

        mutable2._setID("test");
        Assertions.assertEquals("test.004", mutable2.getIdentifier());

        DummyMutableIDValue testAlone = new DummyMutableIDValue("c");
        testAlone._setID("test");
        Assertions.assertEquals("test", testAlone.getIdentifier());
    }

    @Test
    public void testRemoveEventListener() {
        final int[] called = {0};
        IEventListener listener = new EventHelper(event -> called[0]++);
        BaseStructure child = new DummyBaseStructure("aa");

        Event event = this.addEventListeners(child, child, listener);
        child.removeListener(listener);
        this.testTriggerEvent(child, event, called, false);

        event = this.addEventListeners(child, child, listener);
        child.removeListener(Event.class, listener);
        this.testTriggerEvent(child, event, called, true);

        event = new Event(child) {public int hashCode() {return super.hashCode();}};
        child.removeListener(Event.class, listener);
        this.testTriggerEvent(child, event, called, false);
    }

    public void testTriggerEvent(BaseStructure element, Event event, int[] callNum, boolean success) {
        callNum[0] = 0;
        element.triggerEvent(event);
        Assertions.assertEquals(success ? 1 : 0, callNum[0]);
    }

    @Test
    public void testAddEventListener() {
        final int[] called = {0};
        IEventListener listener = new EventHelper(event -> called[0]++);
        BaseStructure child = new DummyBaseStructure("aa");

        this.testTriggerEvent(child, this.addEventListeners(child, child, listener), called, true);

        Event event = new Event(child) {public int hashCode() {
                return super.hashCode();
            }};
        this.testTriggerEvent(child, event, called, true);
    }

    @Test
    public void testRemoveChildEventListener() {
        final int[] called = {0};
        IChildEventListener listener = new EventHelper(event -> called[0]++);
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("aa", ValuePath.Format.PATH);
        BaseStructure child = new DummyBaseStructure("aa");
        parent.add(child);

        Event event = this.addChildEventListeners(parent, child, listener);
        parent.removeChildListener(listener);
        this.testTriggerEvent(child, event, called, false);

        event = this.addChildEventListeners(parent, child, listener);
        parent.removeChildListener(Event.class, listener);
        this.testTriggerEvent(child, event, called, true);

        event = new Event(child) {public int hashCode() {
                return super.hashCode();
            }};
        parent.removeChildListener(Event.class, listener);
        this.testTriggerEvent(child, event, called, false);
    }

    @Test
    public void testAddChildEventListener() {
        final int[] called = {0};
        IChildEventListener listener = new EventHelper(event -> called[0]++);
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("aa", ValuePath.Format.PATH);
        BaseStructure child = new DummyBaseStructure("aa");
        parent.add(child);

        this.testTriggerEvent(child, this.addChildEventListeners(parent, child, listener), called, true);

        this.testTriggerEvent(child, new Event(child) {}, called, true);
        this.testTriggerEvent(parent, new Event(parent) {}, called, false);
    }

    /**
     * @return an event instance to test that is at the lowest hierarchy - lowest subclass
     */
    private Event addChildEventListeners(BaseStructure element, BaseStructure trigger, IChildEventListener listener) {
        element.addChildListener(Event.class, listener);
        element.addChildListener(ChangeEvent.class, listener);
        element.addChildListener(IdentifierChangeEvent.class, listener);

        return new IdentifierChangeEvent(trigger, "", "");
    }

    /**
     * @return an event instance to test that is at the lowest hierarchy - lowest subclass
     */
    private Event addEventListeners(BaseStructure element, BaseStructure trigger, IEventListener listener) {
        element.addListener(Event.class, listener);
        element.addListener(ChangeEvent.class, listener);
        element.addListener(IdentifierChangeEvent.class, listener);

        return new IdentifierChangeEvent(trigger, "", "");
    }
}
