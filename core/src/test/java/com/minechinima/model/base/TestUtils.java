package com.minechinima.model.base;

import com.minechinima.model.utils.ValuePath;

public class TestUtils {
    public static DummyBaseValueHierarchy path(String id) {
        return new DummyBaseValueHierarchy(id, ValuePath.Format.PATH);
    }

    public static DummyBaseValueHierarchy map(String id) {
        return new DummyBaseValueHierarchy(id, ValuePath.Format.MAP);
    }
}
