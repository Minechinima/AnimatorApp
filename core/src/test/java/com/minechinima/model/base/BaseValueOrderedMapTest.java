package com.minechinima.model.base;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.dataoperations.DummyHistoryManager;
import com.minechinima.model.dataoperations.datainterfaces.ListElement;
import com.minechinima.model.values.DoubleValue;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class BaseValueOrderedMapTest {
    @Test
    public void testAddInsertinOrder() {
        BaseValueOrderedMap<DoubleValue> map = this.getAnonymousDoubleMap("test");

        map.add(new DoubleValue("test"));
        map.add(new DoubleValue("test2"));
        map.add(new DoubleValue("test3"));

        Assertions.assertEquals("test", map.get(0).getIdentifier());
        Assertions.assertEquals("test2", map.get(1).getIdentifier());
        Assertions.assertEquals("test3", map.get(2).getIdentifier());
    }

    @Test
    public void testListUUIDOf() {
        BaseValueOrderedMap<DoubleValue> map = this.getAnonymousDoubleMap("test");

        DoubleValue v0 = new DoubleValue("test");
        map.add(v0);
        UUID listUUID0 = map.listUUIDOfIndex(0);

        DoubleValue v1 = new DoubleValue("test2");
        map.add(v1);
        UUID listUUID1 = map.listUUIDOfIndex(1);

        DoubleValue v2 = new DoubleValue("test3");
        map.add(v2);
        UUID listUUID2 = map.listUUIDOfIndex(2);

        DoubleValue v3 = new DoubleValue("test4");
        map.add(v3);
        UUID listUUID3 = map.listUUIDOfIndex(3);

        Assertions.assertEquals("test", map.get(0).getIdentifier());
        Assertions.assertEquals("test2", map.get(1).getIdentifier());
        Assertions.assertEquals("test3", map.get(2).getIdentifier());
        Assertions.assertEquals("test4", map.get(3).getIdentifier());

        Assertions.assertEquals(listUUID0, map.listUUIDOf(v0));
        Assertions.assertEquals(listUUID1, map.listUUIDOf(v1));
        Assertions.assertEquals(listUUID2, map.listUUIDOf(v2));
        Assertions.assertEquals(listUUID3, map.listUUIDOf(v3));
    }

    @Test
    public void testGetByListUUID() {
        BaseValueOrderedMap<DoubleValue> map = this.getAnonymousDoubleMap("test");

        DoubleValue v0 = new DoubleValue("test");
        map.add(v0);
        UUID listUUID0 = map.listUUIDOfIndex(0);

        DoubleValue v1 = new DoubleValue("test2");
        map.add(v1);
        UUID listUUID1 = map.listUUIDOfIndex(1);

        DoubleValue v2 = new DoubleValue("test3");
        map.add(v2);
        UUID listUUID2 = map.listUUIDOfIndex(2);

        DoubleValue v3 = new DoubleValue("test4");
        map.add(v3);
        UUID listUUID3 = map.listUUIDOfIndex(3);

        Assertions.assertEquals("test", map.get(0).getIdentifier());
        Assertions.assertEquals("test2", map.get(1).getIdentifier());
        Assertions.assertEquals("test3", map.get(2).getIdentifier());
        Assertions.assertEquals("test4", map.get(3).getIdentifier());

        Assertions.assertEquals(v0, map.getByListUUID(listUUID0));
        Assertions.assertEquals(v1, map.getByListUUID(listUUID1));
        Assertions.assertEquals(v2, map.getByListUUID(listUUID2));
        Assertions.assertEquals(v3, map.getByListUUID(listUUID3));
    }

    @Test
    public void testUndoRedoAdd() throws UserException {
        BaseValueOrderedMap<DoubleValue> map = this.getAnonymousDoubleMap("test");
        DummyHistoryManager historyManager = new DummyHistoryManager(map);
        map.addLogger(historyManager);

        map.add(new DoubleValue("test", 2));
        UUID listUUID1 = map.listUUIDOfIndex(0);
        historyManager.commitUndoStep();

        map.add(new DoubleValue("test2", 3));
        UUID listUUID2 = map.listUUIDOfIndex(1);
        historyManager.commitUndoStep();

        map.add(new DoubleValue("test3", 4));
        UUID listUUID3 = map.listUUIDOfIndex(2);
        historyManager.commitUndoStep();

        Assertions.assertEquals("test", map.get(0).getIdentifier());
        Assertions.assertEquals("test2", map.get(1).getIdentifier());
        Assertions.assertEquals("test3", map.get(2).getIdentifier());

        historyManager.undo();
        Assertions.assertEquals(2, map.size());
        historyManager.undo();
        Assertions.assertEquals(1, map.size());
        historyManager.undo();
        Assertions.assertEquals(0, map.size());

        historyManager.redo();
        historyManager.redo();
        historyManager.redo();
        Assertions.assertEquals(2, map.get(0).get());
        Assertions.assertEquals("test", map.get(0).getIdentifier());
        Assertions.assertEquals(listUUID1, map.listUUIDOfIndex(0));

        Assertions.assertEquals(3, map.get(1).get());
        Assertions.assertEquals("test2", map.get(1).getIdentifier());
        Assertions.assertEquals(listUUID2, map.listUUIDOfIndex(1));

        Assertions.assertEquals(4, map.get(2).get());
        Assertions.assertEquals("test3", map.get(2).getIdentifier());
        Assertions.assertEquals(listUUID3, map.listUUIDOfIndex(2));
    }

    public BaseValueOrderedMap<DoubleValue> getAnonymousDoubleMap(String id) {
        BaseValueOrderedMap<DoubleValue> map = new BaseValueOrderedMap<DoubleValue>(id) {

            @Override
            public boolean canAdd(int index, ListElement<Object> element) throws UserException {
                return element.value() instanceof DoubleValue;
            }

            @Override
            protected @NotNull DoubleValue instantiate(String id, UUID uuid, BaseType<?> data) {
                DoubleValue v = new DoubleValue(id);
                if (data != null) v.fromData(data);
                return v;
            }
        };
        return map;
    }
}
