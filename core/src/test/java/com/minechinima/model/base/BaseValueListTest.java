package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.dummies.DummyBaseMutableHierarchy;
import com.minechinima.model.base.dummies.DummyBaseStructure;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.datainterfaces.ListElement;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import com.minechinima.model.values.base.BaseValueList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class BaseValueListTest {
    @Test
    public void testArrayElementPath() {
        BaseValueList<BaseStructure> list = new BaseValueList<BaseStructure>("list") {
            @Override
            protected @NotNull BaseStructure instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
                return null;
            }
        };
        DummyBaseStructure i0 = new DummyBaseStructure("test");
        DummyBaseStructure i1 = new DummyBaseStructure("");
        DummyBaseStructure i2 = new DummyBaseStructure("");
        list.add(i0);
        list.add(i1);
        list.add(i2);

        Assertions.assertEquals("[0].test", i0.getRelativePath(list).toValuePath());
        Assertions.assertEquals("[1]", i1.getRelativePath(list).toValuePath());
        Assertions.assertEquals("[2]", i2.getRelativePath(list).toValuePath());

        Assertions.assertEquals("list[0].test", i0.getFullPath().toValuePath());
        Assertions.assertEquals("list[1]", i1.getFullPath().toValuePath());
        Assertions.assertEquals("list[2]", i2.getFullPath().toValuePath());
    }

    @Test
    public void testGetChildWithIndexPath() {
        BaseValueList<BaseStructure> list = new BaseValueList<BaseStructure>("list") {
            @Override
            protected @NotNull BaseStructure instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
                return null;
            }
        };
        DummyBaseStructure i0 = new DummyBaseStructure("test");
        DummyBaseStructure i1 = new DummyBaseStructure("");
        DummyBaseStructure i2 = new DummyBaseStructure("");
        list.add(i0);
        list.add(i1);
        list.add(i2);

        Assertions.assertEquals(i0,list.getChildByValuePath("[0].test"));
        Assertions.assertEquals(i1,list.getChildByValuePath("[1]"));
        Assertions.assertEquals(i2,list.getChildByValuePath("[2]"));
    }

    @Test
    public void testGetChildWithListUUIDPath() {
        BaseValueList<BaseStructure> list = new BaseValueList<BaseStructure>("list") {
            @Override
            protected @NotNull BaseStructure instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
                return null;
            }
        };
        DummyBaseStructure i0 = new DummyBaseStructure("");
        DummyBaseStructure i1 = new DummyBaseStructure("");
        DummyBaseStructure i2 = new DummyBaseStructure("");
        list.add(i0);
        list.add(i1);
        list.add(i2);

        String i0UniquePath = i0.getRelativePath(list).toUniquePath();

        Assertions.assertEquals("[0]", i0.getRelativePath(list).toValuePath());
        list.move(0, 2);
        Assertions.assertEquals("[2]", i0.getRelativePath(list).toValuePath());

        Assertions.assertEquals(i1, list.getChildByValuePath("[0]"));
        Assertions.assertEquals(i2, list.getChildByValuePath("[1]"));
        Assertions.assertEquals(i0, list.getChildByValuePath("[2]"));
        Assertions.assertEquals(i0, list.getChildByUniquePath(i0UniquePath));
    }

    @Test
    public void testMove() {
        BaseValueList<DoubleValue> list = this.getAnonymousDoubleList("test");
        list.add(new DoubleValue("", 0));
        list.add(new DoubleValue("", 1));
        list.add(new DoubleValue("", 2));
        list.add(new DoubleValue("", 3));
        list.add(new DoubleValue("", 4));
        list.add(new DoubleValue("", 5));

        Assertions.assertEquals(2, list.get(2).get());
        Assertions.assertEquals(3, list.get(3).get());
        Assertions.assertEquals(4, list.get(4).get());
        Assertions.assertEquals(5, list.get(5).get());

        list.move(3, 4);
        Assertions.assertEquals(2, list.get(2).get());
        Assertions.assertEquals(4, list.get(3).get());
        Assertions.assertEquals(3, list.get(4).get());
        Assertions.assertEquals(5, list.get(5).get());
        Assertions.assertEquals(6, list.size());

        list.move(2, 5);
        Assertions.assertEquals(4, list.get(2).get());
        Assertions.assertEquals(3, list.get(3).get());
        Assertions.assertEquals(5, list.get(4).get());
        Assertions.assertEquals(2, list.get(5).get());

        list.move(4, 4);
        Assertions.assertEquals(3, list.get(3).get());
        Assertions.assertEquals(5, list.get(4).get());
        Assertions.assertEquals(2, list.get(5).get());
    }

    @Test
    public void testMoveFail() {
        BaseValueList<DoubleValue> list = this.getAnonymousDoubleList("test");
        list.add(new DoubleValue("", 0));
        list.add(new DoubleValue("", 1));
        list.add(new DoubleValue("", 2));

        Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> list.move(0, 3));
    }

    @Test
    public void testPathCollisions() {
        DummyBaseMutableHierarchy map = new DummyBaseMutableHierarchy("root", ValuePath.Format.PATH);
        BaseValueList<DoubleValue> list = this.getAnonymousDoubleList("");
        BaseValueList<DoubleValue> list2 = this.getAnonymousDoubleList("");
        map.add(list, list2);

        DoubleValue v1 = new DoubleValue("", 0);
        DoubleValue v2 = new DoubleValue("", 0);
        DoubleValue v3 = new DoubleValue("", 0);
        list.add(v1);
        list.add(v2);
        /* cant add v3 because then there would be root[0] and v3 creates another root[0] path */
        Assertions.assertThrows(Exception.class, () -> list2.add(v3));
    }

    public static BaseValueList<DoubleValue> getAnonymousDoubleList(String id) {
        BaseValueList<DoubleValue> list = new BaseValueList<>(id) {
            @Override
            public boolean canAdd(int index, ListElement<Object> element) throws UserException {
                return element.value() instanceof DoubleValue;
            }

            @Override
            protected DoubleValue instantiate(String id, UUID uuid, @Nullable BaseType<?> data) {
                DoubleValue v = new DoubleValue(id);
                if (data != null) v.fromData(data);
                return v;
            }
        };

        return list;
    }
}
