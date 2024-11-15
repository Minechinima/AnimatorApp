package com.minechinima.model.base;

import com.minechinima.model.utils.ValuePath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class PathSystemTest {
    @ParameterizedTest
    @MethodSource("com.minechinima.model.utils.ValuePathTest#providePathFormats")
    public void testRelativePathsEmpty(ValuePath.Format pathFormat) {
        final String expectedEnclose = pathFormat.getStart() + "" + pathFormat.getEnd();

        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("", pathFormat);
        BaseStructure h1 = new DummyBaseStructure("");

        root.add(h1);
        if (!root.getPathFormat().isValidStringForEnclosure(h1.getIdentifier())) {
            Assertions.assertNull(h1.getRelativePath(root));
        } else {
            switch (pathFormat.getMode()) {
                case CONCAT:
                    Assertions.assertEquals("", h1.getRelativePath(root).toValuePath());
                    break;
                case ENCLOSE:
                    Assertions.assertEquals(expectedEnclose, h1.getRelativePath(root).toValuePath());
                    break;
            }
        }
    }

    @Test
    public void testRelativePaths2() {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("", ValuePath.Format.MAP);
        DummyBaseValueHierarchy h1 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h2 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h3 = new DummyBaseValueHierarchy("value", ValuePath.Format.PATH);
        DummyBaseValueHierarchy h4 = new DummyBaseValueHierarchy("", ValuePath.Format.PATH);

        h3.add(h4);
        h2.add(h3);
        h1.add(h2);
        root.add(h1);

        Assertions.assertEquals("[\"\"].value", h4.getRelativePath(root).toValuePath());
    }
}
