package com.minechinima.model.lists;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.base.DummyValueGroup;
import com.minechinima.model.base.DummyValueMap;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.dataoperations.DummyHistoryManager;
import com.minechinima.model.values.lists.ValuePointerList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValuePointerListTestUndo {
    @Test
    public void testUndosWithPointerClearing() throws UserException {
        DummyValueGroup root = new DummyValueGroup("root");
        DummyValueMap map = new DummyValueMap("map");
        DummyValueGroup pointerTarget = new DummyValueGroup("test");
        ValuePointerList<DummyValueGroup> list = new ValuePointerList<>("list", DummyValueGroup.class, root, false, false);

        root.add(map, list);
        map.add(pointerTarget);
        ValuePointer<DummyValueGroup> p = new ValuePointer<>("", DummyValueGroup.class, root);
        p.set(pointerTarget);
        list.add(p);

        DummyHistoryManager historyManager = new DummyHistoryManager(root);
        root.addLogger(historyManager);

        map.remove("test");
        historyManager.commitUndoStep();

        //Assertions.assertEquals(0, list.size());
        historyManager.undo();
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(pointerTarget.getUUID(), list.get(0).getReference().getUUID());
    }
}
