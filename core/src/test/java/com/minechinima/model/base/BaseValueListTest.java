package com.minechinima.model.base;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.dataoperations.DummyHistoryManager;
import com.minechinima.model.dataoperations.datainterfaces.ListElement;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class BaseValueListTest {
    @Test
    public void addPathCollisions() {
        BaseValueList<DoubleValue> list = this.getAnonymousDoubleList("test");
        list.add(new DoubleValue("test1", 1));
        list.add(new DoubleValue("test1", 2));
        list.add(new DoubleValue("test1", 3));
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
    }

    @Test
    public void testMoveUndoRedo() throws UserException {
        BaseValueList<DoubleValue> list = this.getAnonymousDoubleList("test");
        DummyHistoryManager history = new DummyHistoryManager(list);
        list.addLogger(history);

        list.add(new DoubleValue("", 0));
        list.add(new DoubleValue("", 1));
        list.add(new DoubleValue("", 2));
        list.add(new DoubleValue("", 3));
        list.add(new DoubleValue("", 4));
        list.add(new DoubleValue("", 5));
        history.commitUndoStep();

        list.move(3, 4);
        history.commitUndoStep();

        Assertions.assertEquals(4, list.get(3).get());
        Assertions.assertEquals(3, list.get(4).get());

        history.undo();
        Assertions.assertEquals(2, list.get(2).get());
        Assertions.assertEquals(3, list.get(3).get());
        Assertions.assertEquals(4, list.get(4).get());
        Assertions.assertEquals(5, list.get(5).get());
        Assertions.assertEquals(6, list.size());
    }

    @Test
    public void fromToData() {
        BaseValueList<DoubleValue> list = getAnonymousDoubleList("");

        list.add(new DoubleValue("", 0D));
        list.add(new DoubleValue("", 1D));
        list.add(new DoubleValue("", 2D));
        list.add(new DoubleValue("", 3D));
        BaseType data = list.toData();
        list.clear();
        Assertions.assertEquals(0, list.size());
        list.fromData(data);
        Assertions.assertEquals(4, list.size());
        for (int i = 0; i < list.size(); i++) {
            Assertions.assertEquals(i, list.get(i).get());
        }
    }

    @Test
    public void getChild() {
        DummyBaseValueHierarchy root1 = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy root2 = new DummyBaseValueHierarchy("root", ValuePath.Format.MAP);
        BaseValueList<DoubleValue> list1 = this.getAnonymousDoubleList("list");
        BaseValueList<DoubleValue> list2 = this.getAnonymousDoubleList("");
        DoubleValue v1 = new DoubleValue("");
        DoubleValue v2 = new DoubleValue("test");
        DoubleValue v3 = new DoubleValue("");
        DoubleValue v4 = new DoubleValue("test");

        list1.add(v1);
        list1.add(v2);

        list2.add(v3);
        list2.add(v4);

        root1.add(list1, list2);

        Assertions.assertEquals(list1, root1.getChildByValuePath("list"));
        Assertions.assertEquals(v1, root1.getChildByValuePath("list[0]"));
        Assertions.assertEquals(v2, root1.getChildByValuePath("list[1].test"));

        Assertions.assertNull(root1.getChildByValuePath(list2.getRelativePath(root1).toValuePath()));
        Assertions.assertEquals(v3, root1.getChildByValuePath("[0]"));
        Assertions.assertEquals(v4, root1.getChildByValuePath("[1].test"));

        list1.setParentNull();
        list2.setParentNull();
        root2.add(list1, list2);

        Assertions.assertEquals(list1, root2.getChildByValuePath(list1.getRelativePath(root2).toValuePath()));
        Assertions.assertEquals(v1, root2.getChildByValuePath("[\"list\"][0]"));
        Assertions.assertEquals(v2, root2.getChildByValuePath("[\"list\"][1].test"));

        Assertions.assertEquals(list2, root2.getChildByValuePath(list2.getRelativePath(root2).toValuePath()));
        Assertions.assertEquals(v3, root2.getChildByValuePath("[\"\"][0]"));
        Assertions.assertEquals(v4, root2.getChildByValuePath("[\"\"][1].test"));
    }

    @Test
    public void testPaths() {
        DummyBaseValueHierarchy map = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        BaseValueList<DoubleValue> list = this.getAnonymousDoubleList("list");
        map.add(list);

        DoubleValue v1 = new DoubleValue("test", 0);
        DoubleValue v2 = new DoubleValue("", 0);
        list.add(v1);
        list.add(v2);

        Assertions.assertEquals("root.list[0].test", v1.getFullPath().toValuePath());
        Assertions.assertEquals("root.list[1]", v2.getFullPath().toValuePath());

        list = this.getAnonymousDoubleList("");
        map.add(list);

        v1 = new DoubleValue("test", 0);
        v2 = new DoubleValue("", 0);
        list.add(v1);
        list.add(v2);

        Assertions.assertEquals("root[0].test", v1.getFullPath().toValuePath());
        Assertions.assertEquals("root[1]", v2.getFullPath().toValuePath());
    }

    @Test
    public void testPathCollisions() {
        DummyBaseValueHierarchy map = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
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
