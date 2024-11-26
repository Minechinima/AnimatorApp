package com.minechinima.model.base;

import com.minechinima.model.base.dummies.*;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.events.bus.EventBusManager;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.ParentChangeEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.memory.ReferenceManager;
import com.minechinima.model.utils.ID;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

public class BaseStructureSetParentTest {
    @Test
    public void setParentSuccess() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("parent", ValuePath.Format.PATH);
        BaseStructure value = new DummyBaseStructure("value");

        parent.children.add(value);
        this.testSetParentSuccess(value, parent);
    }

    /**
     * Test setting the parent of a BaseStructure where children wouldn't adhere to the new pathformat of the parent.
     * @param format
     * @param invalids
     */
    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#provideInvalidPathFormatsExamples")
    public void setParentWithInvalidPathFormat(ValuePath.Format format, List<String> invalids) {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("parent", format);
        for (String invalid : invalids) {
            BaseStructure value = new DummyBaseStructure(invalid);
            parent.children.add(value);
            this.testSetParentFail(value, parent, null);
        }
    }

    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#provideInvalidPathFormatsExamples")
    public void setParentWithInvalidPrefixPathFormat(ValuePath.Format format, List<String> invalids) {
        for (String invalid : invalids) {
            DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("parent", format) {
                @Nullable
                protected ValuePath.Format getPathFormatPrefixForChildren() {
                    return ValuePath.Format.PATH;
                }

                @Nullable
                protected ID getIDPrefixForChild(BaseStructure child) {
                    return new ID(invalid);
                }
            };

            BaseStructure value = new DummyBaseStructure("0");
            parent.children.add(value);
            this.testSetParentFail(value, parent, null);
        }
    }

    /**
     * Test setting the parent of a BaseStructure that has children that would have path collisions with children of
     * the new parent.
     * @param format
     */
    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#providePathFormatsWithConcat")
    public void setParentWithChildCollisionsMutable(ValuePath.Format format) {
        DummyBaseMutableHierarchy root0 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h2_0 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h2_0_1 = new DummyBaseMutableHierarchy("", format);

        root0.add(
                root.add(
                        parent.add(
                                h1.add(
                                        new DummyBaseStructure("duplicate"),
                                        new DummyBaseStructure("value")
                                ))));

        h2.add(
                new DummyBaseStructure("test"),
                h2_0.add(
                        h2_0_1.add(
                                new DummyIDMutable("duplicate")
                        )));

        parent.children.add(h2);
        this.testSetParentFail(h2, parent, "Cannot set parent as children will have path collisions.");
    }

    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#providePathFormatsWithConcat")
    public void setParentWithChildCollisionsNonMutable(ValuePath.Format format) {
        DummyBaseMutableHierarchy root0 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h2_0 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h2_0_1 = new DummyBaseMutableHierarchy("", format);

        root0.add(
                root.add(
                        parent.add(
                                h1.add(
                                        new DummyBaseStructure("duplicate"),
                                        new DummyBaseStructure("value")
                                ))));

        h2.add(
                new DummyBaseStructure("test"),
                h2_0.add(
                        h2_0_1.add(
                                new DummyBaseStructure("duplicate")
                        )));

        parent.children.add(h2);
        this.testSetParentFail(h2, parent, "Cannot set parent as children will have path collisions.");
    }

    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#providePathFormatsWithConcat")
    public void setParentWithCollisionOnNodeNonMutable(ValuePath.Format format) {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", format);
        DummyBaseStructure h1 = new DummyBaseStructure("duplicate");
        DummyBaseStructure h2 = new DummyBaseStructure("duplicate");

        parent.add(h1);

        parent.children.add(h2);
        this.testSetParentFail(h2, parent, "Can't change the id to resolve a path collision of a" +
                " BaseStructure that does not implement IDMutable");
        parent.children.remove(h2);
    }

    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#providePathFormatsWithConcat")
    public void setParentWithCollisionOnNodeIDMutable(ValuePath.Format format) {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", format);
        DummyBaseStructure h1 = new DummyBaseStructure("duplicate");
        DummyIDMutable h2 = new DummyIDMutable("duplicate");

        parent.add(h1);

        parent.children.add(h2);
        this.testSetParentSuccess(h2, parent);
        Assertions.assertEquals("duplicate.001", h2.getIdentifier());
    }

    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#providePathFormatsWithConcat")
    public void setParentWithCollisionOnNodeIDMutableAndChildren(ValuePath.Format format) {
        DummyBaseMutableHierarchy root0 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", format);
        DummyIDMutableHierarchy h2 = new DummyIDMutableHierarchy("test_col", format);
        DummyBaseMutableHierarchy h2_0 = new DummyBaseMutableHierarchy("", format);
        DummyBaseMutableHierarchy h2_0_1 = new DummyBaseMutableHierarchy("", format);

        root0.add(
                root.add(
                        parent.add(
                                new DummyBaseStructure("test_col"),
                                h1.add(
                                        new DummyBaseStructure("test_col.001"),
                                        new DummyBaseStructure("value")
                                ))));

        h2.add(
                new DummyBaseStructure("test"),
                h2_0.add(
                        h2_0_1.add(
                                new DummyBaseStructure("non_duplicate")
                        )));

        parent.children.add(h2);
        /* h2 collides with test_col of parent, which could be resolved, but it also collides with the child test_col of h1 */
        this.testSetParentSuccess(h2, parent);
        Assertions.assertEquals("test_col.002", h2.getIdentifier());
    }

    @Test
    public void setParentNullSuccess() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseStructure h1 = new DummyBaseStructure("value");

        testSetParentSuccess(h1, null);

        parent.children.add(h1);
        testSetParentSuccess(h1, parent);

        parent.children.remove(h1);
        testSetParentSuccess(h1, null);
    }

    @Test
    public void setParentNullFail() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseStructure h1 = new DummyBaseStructure("value");

        parent.children.add(h1);
        testSetParentSuccess(h1, parent);

        testSetParentFail(h1, null, "Setting the parent of a BaseStructure to another element," +
                "while it is still contained in the children of the previous parent, is prohibited to ensure tree integrity.");
    }

    @Test
    public void setParentRecursion() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("value", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("value2", ValuePath.Format.PATH);

        parent.children.add(h1);
        h1.children.add(h2);
        /* h2 -> h1 -> parent */
        testSetParentSuccess(h1, parent);
        testSetParentSuccess(h2, h1);

        /*
         * Try to create
         * parent -> h2 -> h1 -> parent
         */
        h2.children.add(parent);
        testSetParentFail(parent, h2, "Detected loop in parent tree. Can't set parent as it results in recursion.");
    }

    @Test
    public void setParentEvents() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("test", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy parent2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("value", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("value2", ValuePath.Format.PATH);

        root._addChild("test", parent);
        root._addChild("test2", parent2);
        parent.add(h2);
        root.initChildren();

        ReferenceManager referenceManager = new ReferenceManager(root);
        EventTypeRegistry registry = new EventTypeRegistry();
        registry.addEventClass(RemoveFromTreeEvent.class);
        registry.addEventClass(AddToTreeEvent.class);
        registry.addEventClass(ParentChangeEvent.class);
        EventBusManager busManager = new EventBusManager(referenceManager, root, registry);

        int[] called = new int[3];
        busManager.subscribe(root.getEventBus(), new Object() {
            @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
            public void onRemoveFrom(RemoveFromTreeEvent event) {
                called[0]++;
            }

            @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
            public void onAddTo(AddToTreeEvent event) {
                called[1]++;
            }

            @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
            public void onChange(ParentChangeEvent event) {
                called[2]++;
            }
        });

        /* tree add */
        parent.children.add(h1);
        testSetParentSuccess(h1, parent);
        Assertions.assertEquals(0, called[0]);
        Assertions.assertEquals(1, called[1]);
        Assertions.assertEquals(0, called[2]);

        /* parent change in tree */
        parent.children.remove(h1);
        parent2.children.add(h1);
        testSetParentSuccess(h1, parent2);
        Assertions.assertEquals(0, called[0]);
        Assertions.assertEquals(1, called[1]);
        Assertions.assertEquals(1, called[2]);

        /* set parent null */
        parent.children.remove(h2);
        testSetParentSuccess(h2, null);
        Assertions.assertEquals(1, called[0]);
        Assertions.assertEquals(1, called[1]);
        Assertions.assertEquals(1, called[2]);
    }

    @Test
    public void setParentEventChangeTrees() {
        DummyBaseHierarchyRoot root2 = new DummyBaseHierarchyRoot("test", ValuePath.Format.PATH);
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("test", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy parent2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("value", ValuePath.Format.PATH);
        root._addChild("test", parent);
        root.initChildren();
        root2._addChild("test", parent2);
        root2.initChildren();
        parent.add(h1);

        ReferenceManager referenceManager = new ReferenceManager(root);
        ReferenceManager referenceManager2 = new ReferenceManager(root2);
        EventTypeRegistry registry = new EventTypeRegistry();
        registry.addEventClass(RemoveFromTreeEvent.class);
        registry.addEventClass(AddToTreeEvent.class);
        registry.addEventClass(ParentChangeEvent.class);
        EventBusManager busManager = new EventBusManager(referenceManager, root, registry);
        EventBusManager busManager2 = new EventBusManager(referenceManager2, root2, registry);

        int[] called = new int[3];
        busManager.subscribe(root.getEventBus(), new Object() {
            @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
            public void onRemoveFrom(RemoveFromTreeEvent event) {
                called[0]++;
            }

            @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
            public void onAddTo(AddToTreeEvent event) {
                called[1]++;
            }

            @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
            public void onChange(ParentChangeEvent event) {
                called[2]++;
            }
        });

        int[] called2 = new int[3];
        busManager2.subscribe(root2.getEventBus(), new Object() {
            @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
            public void onRemoveFrom(RemoveFromTreeEvent event) {
                called2[0]++;
            }

            @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
            public void onAddTo(AddToTreeEvent event) {
                called2[1]++;
            }

            @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
            public void onChange(ParentChangeEvent event) {
                called2[2]++;
            }
        });

        /* parent change in tree */
        parent.children.remove(h1);
        parent2.children.add(h1);
        testSetParentSuccess(h1, parent2);
        Assertions.assertEquals(1, called[0]);
        Assertions.assertEquals(0, called[1]);
        Assertions.assertEquals(0, called[2]);

        Assertions.assertEquals(0, called2[0]);
        Assertions.assertEquals(1, called2[1]);
        Assertions.assertEquals(0, called2[2]);
    }


    @Test
    public void setParentWhileStillChildOfOld() {
        DummyBaseMutableHierarchy parent1 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy parent2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy v = new DummyBaseMutableHierarchy("value", ValuePath.Format.PATH);

        parent1.add(v);

        testSetParentFail(v, parent2, "Setting the parent of a BaseStructure to another element," +
                "while it is still contained in the children of the previous parent, is prohibited to ensure tree integrity.");
    }

    @Test
    public void setParentWhileNotChildOfNewParent() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy v = new DummyBaseMutableHierarchy("value", ValuePath.Format.PATH);

        testSetParentFail(v, parent, "The new parent needs to have this in its children so the setParent() method can fully simulate" +
                "a new tree state and check if setting the parent is allowed.");
    }

    private void testSetParentSuccess(BaseStructure child, @Nullable BaseValueHierarchy parent) {
        try {
            child.setParent(parent);
        } catch (SetParentNotAllowed e) {
            e.printStackTrace();
            Assertions.fail();
        }

        Assertions.assertEquals(parent, child.getParent());
        if (parent != null) Assertions.assertTrue(parent.getChildren().contains(child));
    }

    private void testSetParentFail(BaseStructure child, @Nullable BaseValueHierarchy parent, @Nullable String errorMessage) {
        BaseValueHierarchy prevParent = child.getParent();
        if (errorMessage != null) {
            try {
                child.setParent(parent);
                Assertions.fail();
            } catch (SetParentNotAllowed e) {
                if (errorMessage != null) {
                    Assertions.assertEquals(e.getMessage(), errorMessage);
                }
            } catch (Exception e) {
                Assertions.fail();
            }
        } else {
            Assertions.assertThrowsExactly(SetParentNotAllowed.class, () -> child.setParent(parent));
        }
        Assertions.assertEquals(prevParent, child.getParent());
    }
}

