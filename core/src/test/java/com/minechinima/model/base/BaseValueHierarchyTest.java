package com.minechinima.model.base;

import com.minechinima.model.utils.ValuePath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

public class BaseValueHierarchyTest {
    @Test
    public void isChildAddressableWithPath() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyMutableIDValue v1 = new DummyMutableIDValue("a");
        DummyMutableIDValue v2 = new DummyMutableIDValue("");

        root.add(h1, v1, v2);

        /* unique path */
        Assertions.assertTrue(root.isNodeAddressableWithUniquePath(v1));
        Assertions.assertTrue(root.isNodeAddressableWithUniquePath(v2));

        Assertions.assertFalse(root.isNodeAddressableWithUniquePath(h1));

        /* value path */
        Assertions.assertTrue(root.isNodeAddressableWithValuePath(v1));

        Assertions.assertFalse(root.isNodeAddressableWithValuePath(h1));
        Assertions.assertFalse(root.isNodeAddressableWithValuePath(v2));
    }

    @Test
    public void invalidTreeState() {
        DummyBaseValueHierarchy parent = new DummyBaseValueHierarchy("parent", ValuePath.Format.PATH);
        DummyBaseStructure value = new DummyBaseStructure("value");

        parent.add(value);
        try {
            Field parentField = BaseStructure.class.getDeclaredField("parent");
            parentField.setAccessible(true);
            parentField.set(value, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Assertions.fail(e.toString());
        }

        Assertions.assertThrowsExactly(IllegalStateException.class, () -> parent.getChildByValuePath("value"));
    }

    @Test
    public void getChildByUniquePath() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("test", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h2 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h3 = new DummyBaseValueHierarchy("", ValuePath.Format.MAP);
        DummyMutableIDValue v1 = new DummyMutableIDValue("a");
        DummyMutableIDValue v2 = new DummyMutableIDValue("b");

        root.add(h1.add(v2), h2.add(h3.add(v1)));
        String path1 = v1.getRelativePath(v1.getRoot()).toUniquePath();
        String path2 = v2.getRelativePath(v2.getRoot()).toUniquePath();

        Assertions.assertEquals(v1, root.getChildByUniquePath(path1));
        Assertions.assertEquals(v2, root.getChildByUniquePath(path2));
        Assertions.assertNull(root.getChildByUniquePath("path2"));
        Assertions.assertEquals(v1, h2.getChildByUniquePath(path1));
    }

    @Test
    public void getChild() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("test", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h2 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h3 = new DummyBaseValueHierarchy("", ValuePath.Format.MAP);
        DummyBaseValueHierarchy empty0 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy empty1 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy map1 = new DummyBaseValueHierarchy("", ValuePath.Format.MAP);
        DummyBaseStructure v1 = new DummyBaseStructure("a");
        DummyBaseStructure v2 = new DummyBaseStructure("b");
        DummyBaseStructure v3 = new DummyBaseStructure("c");
        DummyBaseStructure v4 = new DummyBaseStructure("d");
        DummyBaseStructure v5 = new DummyBaseStructure("d");

        root.add(h1.add(v2), h2.add(h3.add(v1)));
        h2.add(empty0.add(empty1.add(map1.add(v4), v3)));

        root.add(TestUtils.path("test2").add(TestUtils.path("").add(TestUtils.path("")
                        .add(TestUtils.path("test2").add(TestUtils.path("").add(TestUtils.path("").add(v5)))))));

        String path1 = v1.getRelativePath(v1.getRoot()).toValuePath();
        String path2 = v2.getRelativePath(v2.getRoot()).toValuePath();
        String path3 = v3.getRelativePath(v3.getRoot()).toValuePath();
        String path4 = v4.getRelativePath(v4.getRoot()).toValuePath();
        String path5 = v5.getRelativePath(v5.getRoot()).toValuePath();;

        Assertions.assertEquals(v1, root.getChildByValuePath(path1));
        Assertions.assertEquals(v2, root.getChildByValuePath(path2));
        Assertions.assertEquals(v3, root.getChildByValuePath(path3));
        Assertions.assertEquals(v4, root.getChildByValuePath(path4));
        Assertions.assertEquals(v5, root.getChildByValuePath(path5));
        Assertions.assertNull(root.getChildByValuePath("path2"));
        Assertions.assertEquals(v1, h2.getChildByValuePath(path1));
    }

    @Test
    public void getChildrenForPathCollisions() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e0 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e1 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e2 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e3 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e4 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy t0 = new DummyBaseValueHierarchy("test", ValuePath.Format.PATH);
        DummyBaseValueHierarchy t1 = new DummyBaseValueHierarchy("test1", ValuePath.Format.PATH);
        DummyBaseValueHierarchy t2 = new DummyBaseValueHierarchy("test2", ValuePath.Format.PATH);
        DummyBaseValueHierarchy t3 = new DummyBaseValueHierarchy("test3", ValuePath.Format.PATH);

        root.add(e0, e2);
        e0.add(e1);
        e1.add(t0);
        e2.add(e3, t1, t2);
        t2.add(e4);
        e4.add(t3);

        List<BaseStructure> test = root.getChildrenForPathCollisions();

        Assertions.assertTrue(t3.getChildrenForPathCollisions().isEmpty());

        Assertions.assertTrue(test.contains(t0));
        Assertions.assertTrue(test.contains(t1));
        Assertions.assertTrue(test.contains(t2));

        Assertions.assertFalse(test.contains(t3));
        Assertions.assertFalse(test.contains(e0));
        Assertions.assertFalse(test.contains(e1));
        Assertions.assertFalse(test.contains(e2));
        Assertions.assertFalse(test.contains(e3));
        Assertions.assertFalse(test.contains(e4));

        for (BaseStructure element : test) {
            Assertions.assertNotEquals("", element.getRelativePath(root).toValuePath());
        }
    }

    @Test
    public void getPathCollisionContext() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e0 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e1 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e2 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e3 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy e4 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy t0 = new DummyBaseValueHierarchy("test", ValuePath.Format.PATH);
        DummyBaseValueHierarchy t1 = new DummyBaseValueHierarchy("test1", ValuePath.Format.PATH);
        DummyBaseValueHierarchy t2 = new DummyBaseValueHierarchy("test2", ValuePath.Format.PATH);
        DummyBaseValueHierarchy t3 = new DummyBaseValueHierarchy("test3", ValuePath.Format.PATH);

        root.add(e0, e2);
        e0.add(e1);
        e1.add(t0);
        e2.add(e3, t1, t2);
        t2.add(e4);
        e4.add(t3);

        Assertions.assertEquals(t0, t0.getPathCollisionContext());
        Assertions.assertEquals(t1, t1.getPathCollisionContext());
        Assertions.assertEquals(t2, t2.getPathCollisionContext());
        Assertions.assertEquals(t3, t3.getPathCollisionContext());
        Assertions.assertEquals(root, e0.getPathCollisionContext());
        Assertions.assertEquals(root, e1.getPathCollisionContext());
        Assertions.assertEquals(root, e2.getPathCollisionContext());
        Assertions.assertEquals(root, e3.getPathCollisionContext());
        Assertions.assertEquals(t2, e4.getPathCollisionContext());
    }
}
