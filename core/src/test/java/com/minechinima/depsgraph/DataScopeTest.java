package com.minechinima.depsgraph;

import com.minechinima.model.base.dummies.DummyBaseImmutableHierarchy;
import com.minechinima.model.base.dummies.DummyBaseStructure;
import com.minechinima.model.utils.ValuePath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class DataScopeTest {
    @Test
    /**
     * Test case: pass a data tree in the attribute set.
     * Expected behavior: the data tree should not be added to the attribute set but be added to the data trees.
     */
    public void testMixingHierarchiesWithAttributesInit() {
        DummyBaseImmutableHierarchy root = new DummyBaseImmutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1 = new DummyBaseImmutableHierarchy("subTree1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1_1 = new DummyBaseImmutableHierarchy("subTree1_1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree2 = new DummyBaseImmutableHierarchy("subTree2", ValuePath.Format.PATH);
        DummyBaseStructure attribute1 = new DummyBaseStructure("attribute1");
        DummyBaseStructure attribute2 = new DummyBaseStructure("attribute2");
        DummyBaseStructure attribute3 = new DummyBaseStructure("attribute3");
        DummyBaseStructure attribute4 = new DummyBaseStructure("attribute4");
        DummyBaseStructure attribute5 = new DummyBaseStructure("attribute5");
        DummyBaseStructure attribute6 = new DummyBaseStructure("attribute6");

        root._add("subTree1", subTree1);
        root._add("subTree2", subTree2);
        subTree1._add("subTree1", subTree1_1);

        subTree1._add("attribute1", attribute1);
        subTree1._add("attribute2", attribute2);
        subTree2._add("attribute3", attribute3);
        subTree2._add("attribute4", attribute4);
        root._add("attribute5", attribute5);
        root._add("attribute6", attribute6);

        root.initChildren();

        DataScope scope = new DataScope(Set.of(attribute1, attribute2, subTree1), Set.of(subTree2));

        Assertions.assertFalse(scope.getSpecialAttributes().contains(subTree1));
        Assertions.assertTrue(scope.getDataTrees().contains(subTree1));
    }

    /**
     * Test case: pass a subtree of another tree to the data tree set.
     * Expected behavior: the subtree should not be added to the data tree set, as it is already covered by the parent tree.
     */
    @Test
    public void testPassingSubTreesToHierarchiesInit() {
        DummyBaseImmutableHierarchy root = new DummyBaseImmutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1 = new DummyBaseImmutableHierarchy("subTree1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1_1 = new DummyBaseImmutableHierarchy("subTree1_1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree2 = new DummyBaseImmutableHierarchy("subTree2", ValuePath.Format.PATH);
        DummyBaseStructure attribute1 = new DummyBaseStructure("attribute1");
        DummyBaseStructure attribute2 = new DummyBaseStructure("attribute2");
        DummyBaseStructure attribute3 = new DummyBaseStructure("attribute3");
        DummyBaseStructure attribute4 = new DummyBaseStructure("attribute4");
        DummyBaseStructure attribute5 = new DummyBaseStructure("attribute5");
        DummyBaseStructure attribute6 = new DummyBaseStructure("attribute6");

        root._add("subTree1", subTree1);
        root._add("subTree2", subTree2);
        subTree1._add("subTree1", subTree1_1);

        subTree1._add("attribute1", attribute1);
        subTree1._add("attribute2", attribute2);
        subTree2._add("attribute3", attribute3);
        subTree2._add("attribute4", attribute4);
        root._add("attribute5", attribute5);
        root._add("attribute6", attribute6);

        root.initChildren();

        DataScope scope = new DataScope(Set.of(), Set.of(subTree1, subTree1_1));

        Assertions.assertFalse(scope.getDataTrees().contains(subTree1_1));
    }

    /**
     * Test case: pass a subtree of another tree to the attribute tree set.
     * Expected behavior: the subtree should not be added to the data tree set, as it is already covered by the parent tree and also
     * removed from the attribute set.
     */
    @Test
    public void testPassingSubTreesToAttributesInit() {
        DummyBaseImmutableHierarchy root = new DummyBaseImmutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1 = new DummyBaseImmutableHierarchy("subTree1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1_1 = new DummyBaseImmutableHierarchy("subTree1_1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree2 = new DummyBaseImmutableHierarchy("subTree2", ValuePath.Format.PATH);
        DummyBaseStructure attribute1 = new DummyBaseStructure("attribute1");
        DummyBaseStructure attribute2 = new DummyBaseStructure("attribute2");
        DummyBaseStructure attribute3 = new DummyBaseStructure("attribute3");
        DummyBaseStructure attribute4 = new DummyBaseStructure("attribute4");
        DummyBaseStructure attribute5 = new DummyBaseStructure("attribute5");
        DummyBaseStructure attribute6 = new DummyBaseStructure("attribute6");

        root._add("subTree1", subTree1);
        root._add("subTree2", subTree2);
        subTree1._add("subTree1", subTree1_1);

        subTree1._add("attribute1", attribute1);
        subTree1._add("attribute2", attribute2);
        subTree2._add("attribute3", attribute3);
        subTree2._add("attribute4", attribute4);
        root._add("attribute5", attribute5);
        root._add("attribute6", attribute6);

        root.initChildren();

        DataScope scope = new DataScope(Set.of(attribute4, subTree1_1), Set.of(subTree1));

        Assertions.assertFalse(scope.getDataTrees().contains(subTree1_1));
        Assertions.assertFalse(scope.getSpecialAttributes().contains(subTree1_1));

        Assertions.assertTrue(scope.getSpecialAttributes().contains(attribute4));
    }

    /**
     * Test case: pass a set of attributes that are children of a data tree.
     * Expected behavior: the attributes should be removed as they are already children of a data tree.
     */
    @Test
    public void testPassingAttributesAndParentTree() {
        DummyBaseImmutableHierarchy root = new DummyBaseImmutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1 = new DummyBaseImmutableHierarchy("subTree1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1_1 = new DummyBaseImmutableHierarchy("subTree1_1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree2 = new DummyBaseImmutableHierarchy("subTree2", ValuePath.Format.PATH);
        DummyBaseStructure attribute1 = new DummyBaseStructure("attribute1");
        DummyBaseStructure attribute2 = new DummyBaseStructure("attribute2");
        DummyBaseStructure attribute3 = new DummyBaseStructure("attribute3");
        DummyBaseStructure attribute4 = new DummyBaseStructure("attribute4");
        DummyBaseStructure attribute5 = new DummyBaseStructure("attribute5");
        DummyBaseStructure attribute6 = new DummyBaseStructure("attribute6");

        root._add("subTree1", subTree1);
        root._add("subTree2", subTree2);
        subTree1._add("subTree1", subTree1_1);

        subTree1._add("attribute1", attribute1);
        subTree1._add("attribute2", attribute2);
        subTree2._add("attribute3", attribute3);
        subTree2._add("attribute4", attribute4);
        root._add("attribute5", attribute5);
        root._add("attribute6", attribute6);

        root.initChildren();

        DataScope scope = new DataScope(Set.of(attribute1, attribute2, attribute3, attribute4), Set.of(subTree2));

        Assertions.assertTrue(scope.getSpecialAttributes().contains(attribute1));
        Assertions.assertTrue(scope.getSpecialAttributes().contains(attribute2));

        Assertions.assertTrue(scope.getDataTrees().contains(subTree2));

        Assertions.assertFalse(scope.getSpecialAttributes().contains(attribute3));
        Assertions.assertFalse(scope.getSpecialAttributes().contains(attribute4));
    }

    @Test
    public void testIntersectionTrue() {
        DummyBaseImmutableHierarchy root = new DummyBaseImmutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1 = new DummyBaseImmutableHierarchy("subTree1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1_1 = new DummyBaseImmutableHierarchy("subTree1_1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree2 = new DummyBaseImmutableHierarchy("subTree2", ValuePath.Format.PATH);
        DummyBaseStructure attribute1 = new DummyBaseStructure("attribute1");
        DummyBaseStructure attribute2 = new DummyBaseStructure("attribute2");
        DummyBaseStructure attribute3 = new DummyBaseStructure("attribute3");
        DummyBaseStructure attribute4 = new DummyBaseStructure("attribute4");
        DummyBaseStructure attribute5 = new DummyBaseStructure("attribute5");
        DummyBaseStructure attribute6 = new DummyBaseStructure("attribute6");

        root._add("subTree1", subTree1);
        root._add("subTree2", subTree2);
        subTree1._add("subTree1", subTree1_1);

        subTree1._add("attribute1", attribute1);
        subTree1._add("attribute2", attribute2);
        subTree2._add("attribute3", attribute3);
        subTree2._add("attribute4", attribute4);
        root._add("attribute5", attribute5);
        root._add("attribute6", attribute6);

        root.initChildren();

        DataScope scope = new DataScope(Set.of(attribute1, attribute2), Set.of(subTree2));
        DataScope scope1 = new DataScope(Set.of(), Set.of(subTree1));

        Assertions.assertTrue(scope.intersects(scope1));
        Assertions.assertTrue(scope1.intersects(scope));

        DataScope scope2 = new DataScope(Set.of(attribute1, attribute2), Set.of(subTree2));
        DataScope scope3 = new DataScope(Set.of(attribute3), Set.of());

        Assertions.assertTrue(scope3.intersects(scope2));
        Assertions.assertTrue(scope2.intersects(scope3));

        DataScope scope4 = new DataScope(Set.of(attribute1, attribute2), Set.of());
        DataScope scope5 = new DataScope(Set.of(attribute1, attribute2), Set.of());

        Assertions.assertTrue(scope4.intersects(scope5));
        Assertions.assertTrue(scope5.intersects(scope4));

        DataScope scope6 = new DataScope(Set.of(), Set.of(root));
        DataScope scope7 = new DataScope(Set.of(attribute1, attribute2), Set.of());

        Assertions.assertTrue(scope7.intersects(scope6));
        Assertions.assertTrue(scope6.intersects(scope7));
    }

    @Test
    public void testIntersectionFalse() {
        DummyBaseImmutableHierarchy root = new DummyBaseImmutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1 = new DummyBaseImmutableHierarchy("subTree1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1_1 = new DummyBaseImmutableHierarchy("subTree1_1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree2 = new DummyBaseImmutableHierarchy("subTree2", ValuePath.Format.PATH);
        DummyBaseStructure attribute1 = new DummyBaseStructure("attribute1");
        DummyBaseStructure attribute2 = new DummyBaseStructure("attribute2");
        DummyBaseStructure attribute3 = new DummyBaseStructure("attribute3");
        DummyBaseStructure attribute4 = new DummyBaseStructure("attribute4");
        DummyBaseStructure attribute5 = new DummyBaseStructure("attribute5");
        DummyBaseStructure attribute6 = new DummyBaseStructure("attribute6");

        root._add("subTree1", subTree1);
        root._add("subTree2", subTree2);
        subTree1._add("subTree1", subTree1_1);

        subTree1._add("attribute1", attribute1);
        subTree1._add("attribute2", attribute2);
        subTree2._add("attribute3", attribute3);
        subTree2._add("attribute4", attribute4);
        root._add("attribute5", attribute5);
        root._add("attribute6", attribute6);

        root.initChildren();

        DataScope scope = new DataScope(Set.of(attribute1, attribute2), Set.of());
        DataScope scope1 = new DataScope(Set.of(), Set.of(subTree2));
        Assertions.assertFalse(scope.intersects(scope1));
        Assertions.assertFalse(scope1.intersects(scope));

        DataScope scope2 = new DataScope(Set.of(attribute1, attribute2), Set.of());
        DataScope scope3 = new DataScope(Set.of(attribute3, attribute4), Set.of());
        Assertions.assertFalse(scope3.intersects(scope2));
        Assertions.assertFalse(scope2.intersects(scope3));

        DataScope scope4 = new DataScope(Set.of(attribute2), Set.of());
        DataScope scope5 = new DataScope(Set.of(), Set.of(subTree2));
        Assertions.assertFalse(scope5.intersects(scope4));
        Assertions.assertFalse(scope4.intersects(scope5));
    }

    @Test
    public void testInScope() {
        DummyBaseImmutableHierarchy root = new DummyBaseImmutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1 = new DummyBaseImmutableHierarchy("subTree1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree1_1 = new DummyBaseImmutableHierarchy("subTree1_1", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy subTree2 = new DummyBaseImmutableHierarchy("subTree2", ValuePath.Format.PATH);
        DummyBaseStructure attribute1 = new DummyBaseStructure("attribute1");
        DummyBaseStructure attribute2 = new DummyBaseStructure("attribute2");
        DummyBaseStructure attribute3 = new DummyBaseStructure("attribute3");
        DummyBaseStructure attribute4 = new DummyBaseStructure("attribute4");
        DummyBaseStructure attribute5 = new DummyBaseStructure("attribute5");
        DummyBaseStructure attribute6 = new DummyBaseStructure("attribute6");
        DummyBaseStructure attribute7 = new DummyBaseStructure("attribute7");
        DummyBaseStructure attribute8 = new DummyBaseStructure("attribute8");

        root._add("subTree1", subTree1);
        root._add("subTree2", subTree2);
        subTree1._add("subTree1", subTree1_1);

        subTree1._add("attribute1", attribute1);
        subTree1._add("attribute2", attribute2);
        subTree2._add("attribute3", attribute3);
        subTree2._add("attribute4", attribute4);
        subTree1_1._add("attribute7", attribute7);
        subTree1_1._add("attribute8", attribute8);
        root._add("attribute5", attribute5);
        root._add("attribute6", attribute6);

        root.initChildren();

        DataScope scope = new DataScope(Set.of(), Set.of(root));
        Assertions.assertTrue(scope.isInScope(root));
        Assertions.assertTrue(scope.isInScope(subTree2));
        Assertions.assertTrue(scope.isInScope(subTree1_1));
        Assertions.assertTrue(scope.isInScope(attribute8));


        DataScope scope2 = new DataScope(Set.of(), Set.of(subTree1_1, subTree2));
        Assertions.assertTrue(scope2.isInScope(attribute8));
        Assertions.assertTrue(scope2.isInScope(attribute3));
        Assertions.assertTrue(scope2.isInScope(subTree2));
        Assertions.assertFalse(scope2.isInScope(attribute5));
    }
}
