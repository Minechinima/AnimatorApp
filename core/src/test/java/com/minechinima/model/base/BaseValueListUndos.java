package com.minechinima.model.base;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.dataoperations.DummyHistoryManager;
import com.minechinima.model.values.DoubleValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BaseValueListUndos {
    @Test
    public void removeChildUndo() throws UserException {
        BaseValueList<DoubleValue> list = BaseValueListTest.getAnonymousDoubleList("test");

        DummyValueGroup root = new DummyValueGroup("root");
        root.add(list);
        list.add(new DoubleValue("", 1D));
        list.add(new DoubleValue("", 2D));
        list.add(new DoubleValue("", 3D));

        DummyHistoryManager manager = new DummyHistoryManager(root);
        root.addLogger(manager);

        list.remove(1);
        manager.commitUndoStep();
        manager.undo();
        List<DoubleValue> children = list.getChildren();
        Assertions.assertEquals(3, children.size());
        Assertions.assertEquals(2, list.get(1).get());
    }

    @Test
    public void removeChildUndoCollab() throws UserException {
        BaseValueList<DoubleValue> list = BaseValueListTest.getAnonymousDoubleList("test");

        DummyValueGroup root = new DummyValueGroup("root");
        root.add(list);
        list.add(new DoubleValue("", 1D));
        list.add(new DoubleValue("", 2D));
        list.add(new DoubleValue("", 3D));

        DummyHistoryManager manager = new DummyHistoryManager(root);
        root.addLogger(manager);

        list.remove(1);
        manager.commitUndoStep();

        /* collab simulation */
        manager.setRecording(false);
        list.add(new DoubleValue("", 4D));
        list.add(new DoubleValue("", 5D));
        manager.setRecording(true);

        Assertions.assertEquals(1, manager.getUndos().size());
        Assertions.assertEquals(1, manager.getUndos().peek().size());

        manager.undo();
        Assertions.assertEquals(1, manager.getRedos().size());
        Assertions.assertEquals(1, manager.getRedos().peek().size());

        List<DoubleValue> children = list.getChildren();
        Assertions.assertEquals(5, children.size());
        Assertions.assertEquals(2, list.get(1).get());

        manager.setRecording(false);
        list.add(0, new DoubleValue("", 6D));
        list.add(1, new DoubleValue("", 7D));
        manager.setRecording(true);

        manager.redo();

        Assertions.assertEquals(1, manager.getUndos().size());
        Assertions.assertEquals(1, manager.getUndos().peek().size());

        children = list.getChildren();
        Assertions.assertEquals(6, children.size());
        Assertions.assertEquals(7, list.get(1).get());
        Assertions.assertEquals(1, list.get(2).get());
        Assertions.assertEquals(3, list.get(3).get());
    }
}
