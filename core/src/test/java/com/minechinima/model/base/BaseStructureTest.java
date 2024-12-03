package com.minechinima.model.base;

import com.minechinima.model.base.dummies.DummyBaseImmutableHierarchy;
import com.minechinima.model.base.dummies.DummyBaseMutableHierarchy;
import com.minechinima.model.base.dummies.DummyBaseStructure;
import com.minechinima.model.utils.ValuePath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BaseStructureTest {
    @Test
    public void isInSameTreeTrue() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h33 = new DummyBaseMutableHierarchy("h33", ValuePath.Format.PATH);

        h1.add(h2.add(h3, h33));

        Assertions.assertTrue(h3.isInSameTree(h33));
        Assertions.assertTrue(h33.isInSameTree(h3));

        Assertions.assertTrue(h3.isInSameTree(h2));
        Assertions.assertTrue(h3.isInSameTree(h1));
        Assertions.assertTrue(h3.isInSameTree(h3));
    }

    @Test
    public void isInSameTreeFalse() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h33 = new DummyBaseMutableHierarchy("h33", ValuePath.Format.PATH);

        h1.add(h2.add(h3));

        Assertions.assertFalse(h3.isInSameTree(h33));
        Assertions.assertFalse(h33.isInSameTree(h3));
    }

    @Test
    public void findFirstParentFoundDirectParent() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h4 = new DummyBaseMutableHierarchy("h4", ValuePath.Format.PATH);

        h1.add(
                h2.add(
                        h3.add(
                                h4)));
        Assertions.assertEquals(h3, h4.findFirstParent(parent -> parent.getIdentifier().equals("h3")));
    }

    @Test
    public void isChildOfTrue() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);

        h1.add(
                h2.add(
                        h3));

        Assertions.assertTrue(h3.isChildOf(h1));
        Assertions.assertTrue(h3.isChildOf(h2));
    }

    @Test
    public void isChildOfFalse() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);

        DummyBaseMutableHierarchy test = new DummyBaseMutableHierarchy("test", ValuePath.Format.PATH);

        h1.add(
                h2.add(
                        h3.add(test)));

        Assertions.assertFalse(h3.isChildOf(h3));
        Assertions.assertFalse(h3.isChildOf(test));
        Assertions.assertFalse(h3.isChildOf(new DummyBaseImmutableHierarchy("a", ValuePath.Format.PATH)));
    }

    @Test
    public void findFirstParentFoundAny() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h4 = new DummyBaseMutableHierarchy("h4", ValuePath.Format.PATH);

        h1.add(
                h2.add(
                        h3.add(
                                h4)));
        Assertions.assertEquals(h2, h4.findFirstParent(parent -> parent.getIdentifier().equals("h2")));
    }

    @Test
    public void findFirstParentFoundRoot() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h4 = new DummyBaseMutableHierarchy("h4", ValuePath.Format.PATH);

        root.add(h1.add(
                    h2.add(
                            h3.add(
                                    h4))));
        Assertions.assertEquals(root, h4.findFirstParent(parent -> parent.getIdentifier().equals("root")));
    }

    @Test
    public void findFirstParentNotFound() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h4 = new DummyBaseMutableHierarchy("h4", ValuePath.Format.PATH);

        h1.add(
                h2.add(
                        h3.add(
                                h4)));
        Assertions.assertEquals(null, h4.findFirstParent(parent -> parent.getIdentifier().equals("")));
    }

    @Test
    public void findFirstParentTraceFoundRoot() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h4 = new DummyBaseMutableHierarchy("h4", ValuePath.Format.PATH);

        h1.add(
                h2.add(
                        h3.add(
                                h4)));

        List<BaseValueHierarchy<?>> parents = h4.findFirstParentTrace(parent -> parent.getIdentifier().equals("h1"));
        Assertions.assertNotNull(parents);

        Assertions.assertEquals(h1, parents.get(2));
        Assertions.assertEquals(h2, parents.get(1));
        Assertions.assertEquals(h3, parents.get(0));
        Assertions.assertEquals(3, parents.size());
    }

    @Test
    public void findFirstParentTraceNotFound() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h4 = new DummyBaseMutableHierarchy("h4", ValuePath.Format.PATH);

        h1.add(
                h2.add(
                        h3.add(
                                h4)));

        List<BaseValueHierarchy<?>> parents = h4.findFirstParentTrace(parent -> parent.getIdentifier().equals(""));
        Assertions.assertNull(parents);
    }

    @Test
    public void findFirstParentTraceFoundFirstParent() {
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("h1", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("h2", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("h3", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h4 = new DummyBaseMutableHierarchy("h4", ValuePath.Format.PATH);

        h1.add(
                h2.add(
                        h3.add(
                                h4)));

        List<BaseValueHierarchy<?>> parents = h4.findFirstParentTrace(parent -> parent.getIdentifier().equals("h3"));
        Assertions.assertNotNull(parents);
        Assertions.assertEquals(1, parents.size());
        Assertions.assertEquals(h3, parents.get(0));
    }
}
