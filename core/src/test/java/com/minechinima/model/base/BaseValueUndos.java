package com.minechinima.model.base;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.dataoperations.DummyHistoryManager;
import com.minechinima.model.events.IEventListener;
import com.minechinima.model.events.changes.ValueChangeEvent;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BaseValueUndos {
    @Test
    public void testDoubleValueUndo() throws UserException {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy objects = new DummyBaseValueHierarchy("objects", ValuePath.Format.MAP);
        DummyBaseValueHierarchy objectA = new DummyBaseValueHierarchy("MyObject", ValuePath.Format.PATH);
        DoubleValue x = new DoubleValue("x", 0D);
        DoubleValue y = new DoubleValue("y", 0D);
        DoubleValue z = new DoubleValue("z", 0D);

        objectA.add(x, y, z);

        root.add(objects.add(objectA));

        DummyHistoryManager manager = new DummyHistoryManager(root);
        root.addLogger(manager);

        x.set(1.0);
        manager.commitUndoStep();
        x.set(2.0);
        manager.commitUndoStep();
        y.set(1.0);
        manager.commitUndoStep();

        Assertions.assertEquals(x.get(), 2);
        Assertions.assertEquals(y.get(), 1);

        manager.undo();
        Assertions.assertEquals(x.get(), 2);
        Assertions.assertEquals(y.get(), 0);

        manager.undo();
        Assertions.assertEquals(x.get(), 1);
        Assertions.assertEquals(y.get(), 0);

        manager.undo();
        Assertions.assertEquals(x.get(), 0);
        Assertions.assertEquals(y.get(), 0);

        manager.redo();
        Assertions.assertEquals(x.get(), 1);
        Assertions.assertEquals(y.get(), 0);

        manager.redo();
        Assertions.assertEquals(x.get(), 2);
        Assertions.assertEquals(y.get(), 0);

        manager.undo();
        Assertions.assertEquals(x.get(), 1);
        Assertions.assertEquals(y.get(), 0);

        z.set(3D);
        manager.commitUndoStep();
        manager.redo();
        Assertions.assertEquals(x.get(), 1);
        Assertions.assertEquals(y.get(), 0);
        Assertions.assertEquals(z.get(), 3);

        manager.undo();
        Assertions.assertEquals(x.get(), 1);
        Assertions.assertEquals(y.get(), 0);
        Assertions.assertEquals(z.get(), 0);

        manager.undo();
        Assertions.assertEquals(x.get(), 0);
        Assertions.assertEquals(y.get(), 0);
        Assertions.assertEquals(z.get(), 0);
    }

    //TODO
    public void testUndoMerge() throws UserException {
        DummyValueGroup root = new DummyValueGroup("root");
        DoubleValue test = new DoubleValue("v");

        root.add(test);

        DummyHistoryManager manager = new DummyHistoryManager(root);

        IEventListener<ValueChangeEvent> fn = new EventHelper<>((event) -> event.getDataOperation().enableMerge());
        test.addListener(ValueChangeEvent.class, fn);
        test.set(1D);
        manager.commitUndoStep();
        test.removeListener(fn);

        test.addListener(ValueChangeEvent.class, fn);
        test.set(2D);
        manager.commitUndoStep();
        test.removeListener(fn);

        test.addListener(ValueChangeEvent.class, fn);
        test.set(3D);
        manager.commitUndoStep();
        test.removeListener(fn);

        Assertions.assertEquals(3D, test.get());
        manager.undo();
        Assertions.assertEquals(0D, test.get());
    }

    @Test
    public void mergeUndos() throws UserException {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy objects = new DummyBaseValueHierarchy("objects", ValuePath.Format.MAP);
        DummyValueGroup objectA = new DummyValueGroup("MyObject");

        root.add(objects.add(objectA));

        DummyHistoryManager manager = new DummyHistoryManager(root);
        root.addLogger(manager);

        objectA.x.set(1D);
        objectA.x.set(2D);
        objectA.x.set(3D);
        objectA.x.set(4D);
        objectA.setID("test");
        manager.commitUndoStep();
        Assertions.assertEquals(1, manager.getUndos().size());
        Assertions.assertEquals(5, manager.getUndos().peek().size());
        manager.undo();
        Assertions.assertEquals(1, manager.getRedos().size());
        Assertions.assertEquals(5, manager.getRedos().peek().size());
        Assertions.assertEquals(0, objectA.x.get());
        Assertions.assertEquals("MyObject", objectA.getIdentifier());
    }

    @Test
    public void testDoubleValueUndoCollaborative() throws UserException {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyBaseValueHierarchy objects = new DummyBaseValueHierarchy("objects", ValuePath.Format.MAP);
        DummyValueGroup objectA = new DummyValueGroup("MyObject");

        root.add(objects.add(objectA));

        DummyHistoryManager manager = new DummyHistoryManager(root);
        root.addLogger(manager);

        objectA.x.set(1D);
        manager.commitUndoStep();
        objectA.x.set(2D);
        manager.commitUndoStep();

        manager.setRecording(false);
        objectA.x.set(3D);
        manager.setRecording(true);

        manager.undo();
        Assertions.assertEquals(objectA.x.get(), 1);
        Assertions.assertEquals(objectA.y.get(), 0);
        Assertions.assertEquals(objectA.z.get(), 0);

        manager.redo();
        Assertions.assertEquals(objectA.x.get(), 3);
        Assertions.assertEquals(objectA.y.get(), 0);
        Assertions.assertEquals(objectA.z.get(), 0);

        manager.undo();
        Assertions.assertEquals(objectA.x.get(), 1);
        Assertions.assertEquals(objectA.y.get(), 0);
        Assertions.assertEquals(objectA.z.get(), 0);
    }
}
