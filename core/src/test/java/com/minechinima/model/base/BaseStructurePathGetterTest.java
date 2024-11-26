package com.minechinima.model.base;

import com.minechinima.model.base.dummies.DummyBaseMutableHierarchy;
import com.minechinima.model.base.dummies.DummyBaseStructure;
import com.minechinima.model.utils.ID;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BaseStructurePathGetterTest {
    @Test
    public void getFullPath() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("parent", ValuePath.Format.MAP);
        BaseStructure value = new DummyBaseStructure("value");

        root.add(h1.add(h2.add(h3.add(value))));

        Assertions.assertEquals("root.parent[\"value\"]", value.getFullPath().toValuePath());
        Assertions.assertEquals("root.parent[\"value\"]", value.getFullPath().toUniquePath());
    }

    @Test
    public void getFullPathWithoutParents() {
        BaseStructure value = new DummyBaseStructure("value");

        Assertions.assertEquals("value", value.getFullPath().toValuePath());
        Assertions.assertEquals("value", value.getFullPath().toUniquePath());
    }

    @Test
    public void getRelativePathNull() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("parent", ValuePath.Format.MAP);
        BaseStructure value = new DummyBaseStructure("value");

        root.add(h1.add(h2.add(h3.add(value))));

        Assertions.assertEquals("root.parent[\"value\"]", value.getRelativePath(null).toValuePath());
        Assertions.assertEquals("root.parent[\"value\"]", value.getRelativePath(null).toUniquePath());
    }

    @Test
    public void getRelativePathToSelf() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("parent", ValuePath.Format.MAP);
        DummyBaseMutableHierarchy value = new DummyBaseMutableHierarchy("value", ValuePath.Format.PATH);

        root.add(h1.add(h2.add(h3.add(value))));

        Assertions.assertNull(value.getRelativePath(value));
    }

    @Test
    public void getRelativePathNotFound() {
        DummyBaseMutableHierarchy value = new DummyBaseMutableHierarchy("value", ValuePath.Format.PATH);
        Assertions.assertNull(value.getRelativePath(new DummyBaseMutableHierarchy("value", ValuePath.Format.PATH)));
    }

    @Test
    public void getRelativePathWithPrefix() {
        DummyBaseMutableHierarchy root = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h3 = new DummyBaseMutableHierarchy("parent", ValuePath.Format.MAP) {
            @Override
            protected ValuePath.Format getPathFormatPrefixForChildren() {
                return ValuePath.Format.MAP;
            }

            @Override
            protected ID getIDPrefixForChild(BaseStructure child) {
                return new ID("0");
            }
        };
        DummyBaseMutableHierarchy value = new DummyBaseMutableHierarchy("value", ValuePath.Format.PATH);

        root.add(h1.add(h2.add(h3.add(value))));

        Assertions.assertEquals("root.parent[\"0\"][\"value\"]", value.getRelativePath(root).toValuePath());
    }
}
