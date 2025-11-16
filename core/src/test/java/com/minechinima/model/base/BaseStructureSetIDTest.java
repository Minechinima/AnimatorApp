package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.dummies.DummyBaseMutableHierarchy;
import com.minechinima.model.base.dummies.DummyBaseStructure;
import com.minechinima.model.base.dummies.DummyIDMutable;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.operations.IdentifierChange;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.base.BaseValueList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class BaseStructureSetIDTest {
    @Test
    public void _setIDLogging() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("test", ValuePath.Format.MAP);
        DummyIDMutable v = new DummyIDMutable("a");
        DataOperationLog log = new DataOperationLog();
        parent.getDataOperationBus().registerLog(log);

        parent.add(v);

        testSetIDSuccess(v, "test");
        Assertions.assertTrue(log.getLog().pop().getOperation() instanceof IdentifierChange);
    }
    @Test
    public void _setID() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("test", ValuePath.Format.MAP);
        DummyIDMutable v = new DummyIDMutable("a");

        parent.add(v);

        testSetIDSuccess(v, "test");
        testSetIDSuccess(v, "");

        BaseValueList<DummyIDMutable> list = new BaseValueList<>("") {
            @Override
            protected @NotNull DummyIDMutable instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
                return null;
            }
        };
        DummyIDMutable v2 = new DummyIDMutable("a");
        list.add(v2);

        v2._setID("test");
        v2._setID("");
    }

    @Test
    public void _setIDOnNonMutable() {
        DummyBaseStructure v = new DummyBaseStructure("a");

        testSetIDFail(v, "test", IllegalStateException.class,
                "You cannot change the ID of a BaseStructure that does not implement IDMutable interface.");
    }

    @Test
    public void _setIDEmptyAllowed() {
        DummyIDMutable v = new DummyIDMutable("a");

        testSetIDSuccess(v, "");
    }

    @Test
    public void _setIDEmptyNotAllowed() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("test", ValuePath.Format.PATH);
        DummyIDMutable v = new DummyIDMutable("a");
        parent.add(v);
        testSetIDFail(v, "", IllegalArgumentException.class,
                "Changing a non empty id to empty is not allowed when there is a parent.");
    }

    @Test
    public void _setIDFormattingError() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("test", ValuePath.Format.ARRAY);
        DummyIDMutable v = new DummyIDMutable("0");
        parent.add(v);
        testSetIDFail(v, "", IllegalArgumentException.class,
                "The given id is not a valid string for the format!");
    }

    @Test
    public void _setIDCollisionsResolve() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyIDMutable mutable = new DummyIDMutable("b");
        DummyIDMutable mutable2 = new DummyIDMutable("c");

        parent.add(new DummyBaseStructure("test"),
                new DummyBaseStructure("test.001"),
                h1.add(new DummyBaseStructure("test.002"),
                        mutable2),
                mutable);

        testSetIDCollisionResolvedSuccess(mutable, "test", "test.003");
        testSetIDCollisionResolvedSuccess(mutable2, "test", "test.004");
    }

    @Test
    public void _setIDCollisionsResolveMultipleTimes() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyIDMutable mutable = new DummyIDMutable("b");
        DummyIDMutable mutable2 = new DummyIDMutable("c");

        parent.add(new DummyBaseStructure("test"),
                new DummyBaseStructure("test.001"),
                h1.add(new DummyBaseStructure("test.002"),
                        mutable2),
                mutable);

        testSetIDCollisionResolvedSuccess(mutable, "test", "test.003");
        testSetIDCollisionResolvedSuccess(mutable2, "test", "test.004");
        testSetIDCollisionResolvedSuccess(mutable2, "test", "test.004");
        testSetIDCollisionResolvedSuccess(mutable2, "test.004", "test.004");
        testSetIDCollisionResolvedSuccess(mutable2, "test.004", "test.004");
    }

    @Test
    public void _setIDCollisionsResolveFirstSuffixFree() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyIDMutable mutable = new DummyIDMutable("b");
        DummyIDMutable mutable2 = new DummyIDMutable("c");

        parent.add(new DummyBaseStructure("test.001"),
                h1.add(new DummyBaseStructure("test.002"),
                        mutable2),
                mutable);

        testSetIDCollisionResolvedSuccess(mutable, "test", "test");
        testSetIDCollisionResolvedSuccess(mutable2, "test", "test.003");
    }

    @Test
    public void _setIDCollisionsResolveMAPRandomSuffixFree() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", ValuePath.Format.MAP);
        DummyIDMutable mutable = new DummyIDMutable("b");
        DummyIDMutable mutable2 = new DummyIDMutable("c");

        parent.add(
                h1.add(new DummyBaseStructure("test"),
                        new DummyBaseStructure("test.002"),
                        mutable2, mutable));

        testSetIDCollisionResolvedSuccess(mutable, "test", "test.001");
        testSetIDCollisionResolvedSuccess(mutable2, "test", "test.003");
    }

    @Test
    public void _setIDCollisionsResolveRandomSuffixFree() {
        DummyBaseMutableHierarchy parent = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyIDMutable mutable = new DummyIDMutable("b");
        DummyIDMutable mutable2 = new DummyIDMutable("c");

        parent.add(new DummyBaseStructure("test"),
                h1.add(new DummyBaseStructure("test.002"),
                        mutable2),
                mutable);

        testSetIDCollisionResolvedSuccess(mutable, "test", "test.001");
        testSetIDCollisionResolvedSuccess(mutable2, "test", "test.003");
    }

    private static void testSetIDCollisionResolvedSuccess(BaseStructure node, String newID, String resolved) {
        node._setID(newID);
        Assertions.assertEquals(resolved, node.getIdentifier());
        Assertions.assertEquals(resolved, node.getID().id());
    }

    private static void testSetIDSuccess(BaseStructure node, String newID) {
        node._setID(newID);
        Assertions.assertEquals(newID, node.getIdentifier());
        Assertions.assertEquals(newID, node.getID().id());
    }

    private static void testSetIDFail(BaseStructure node, String newID, Class<? extends Exception> exception, @Nullable String message) {
        final String prevID = node.getIdentifier();

        try {
            node._setID(newID);
            Assertions.fail("No Exception was thrown");
        } catch (Exception e) {
            if (!e.getClass().equals(exception)) {
                Assertions.fail("Exception class does not match.");
            }
            if (message != null) {
                Assertions.assertEquals(message, e.getMessage());
            }
        }

        Assertions.assertEquals(prevID, node.getIdentifier());
        Assertions.assertEquals(prevID, node.getID().id());
    }
}
