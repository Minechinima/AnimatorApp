package com.minechinima.model.base;

import com.minechinima.model.base.dummies.DummyBaseSingleValueHierarchy;
import com.minechinima.model.base.dummies.DummyBaseStructure;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BaseSingleValueHierarchyTest {
    @Test
    public void setSuccess() {
        DummyBaseSingleValueHierarchy test = new DummyBaseSingleValueHierarchy("");
        setSuccess(test, new DummyBaseStructure("a"));
    }

    @Test
    public void setNullToNull() {
        DummyBaseSingleValueHierarchy test = new DummyBaseSingleValueHierarchy("");
        setSuccess(test, null);
    }

    @Test
    public void setNonNullToNull() {
        DummyBaseSingleValueHierarchy test = new DummyBaseSingleValueHierarchy("");
        setSuccess(test, new DummyBaseStructure("test"));
        setSuccess(test, null);
    }

    @Test
    public void addChildSuccess() {
        DummyBaseSingleValueHierarchy test = new DummyBaseSingleValueHierarchy("");
        BaseStructure val = new DummyBaseStructure("");
        Assertions.assertTrue(test.addChild(val));
        BaseStructureSetParentTest.testIsChildOfParent(val, test);
    }

    @Test
    public void addChildNullFail() {
        DummyBaseSingleValueHierarchy test = new DummyBaseSingleValueHierarchy("");
        BaseStructure prev = new DummyBaseStructure("");
        setSuccess(test, prev);
        Assertions.assertFalse(test.addChild(null));
        Assertions.assertEquals(prev, test.get());
    }

    @Test
    public void addChildFail() {
        DummyBaseSingleValueHierarchy test = new DummyBaseSingleValueHierarchy("");
        BaseStructure prev = new DummyBaseStructure("");
        setSuccess(test, prev);
        Assertions.assertFalse(test.addChild(new DummyBaseStructure("")));
        Assertions.assertEquals(prev, test.get());
    }

    @Test
    public void removeChildSuccess() {
        DummyBaseSingleValueHierarchy test = new DummyBaseSingleValueHierarchy("");
        BaseStructure val = new DummyBaseStructure("");
        setSuccess(test, val);
        Assertions.assertTrue(test.removeChild(val));
        BaseStructureSetParentTest.testIsNotChildOfParent(val, test);
    }

    @Test
    public void removeChildNullFail() {
        DummyBaseSingleValueHierarchy test = new DummyBaseSingleValueHierarchy("");
        BaseStructure prev = new DummyBaseStructure("");
        setSuccess(test, prev);
        Assertions.assertFalse(test.removeChild(null));
        Assertions.assertEquals(prev, test.get());
    }

    @Test
    public void removeChildFail() {
        DummyBaseSingleValueHierarchy test = new DummyBaseSingleValueHierarchy("");
        BaseStructure prev = new DummyBaseStructure("");
        setSuccess(test, prev);
        Assertions.assertFalse(test.removeChild(new DummyBaseStructure("")));
        Assertions.assertEquals(prev, test.get());
    }

    private static void setSuccess(BaseSingleValueHierarchy hierarchy, BaseStructure value) {
        hierarchy.set(value);
        Assertions.assertEquals(value, hierarchy.get());
        if (value != null) {
            BaseStructureSetParentTest.testIsChildOfParent(value, hierarchy);
            Assertions.assertTrue(hierarchy.getChildren().size() == 1);
        }
    }
}
