package com.minechinima.model.base;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.dataoperations.DummyHistoryManager;
import com.minechinima.model.utils.ValuePath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DynamicHierarchyUndos {
    @Test
    public void undoAdd() throws UserException {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyValueMap objects = new DummyValueMap("objects");

        DummyValueGroup objectA = new DummyValueGroup("MyObject");
        objectA.x.set(1D);
        objectA.y.set(2D);
        objectA.z.set(3D);

        root.add(objects);

        DummyHistoryManager manager = new DummyHistoryManager(root);
        root.addLogger(manager);


        objects.add(objectA);
        manager.commitUndoStep();

        Assertions.assertEquals(1, manager.getUndos().size());
        Assertions.assertEquals(1, manager.getUndos().peek().size());

        manager.undo();

        Assertions.assertEquals(1, manager.getRedos().size());
        Assertions.assertEquals(1, manager.getRedos().peek().size());

        Assertions.assertTrue(objects.getChildren().isEmpty());
        manager.redo();
        Assertions.assertEquals(1, objects.getChildren().size());
        Assertions.assertEquals(1, objects.getChildren().iterator().next().x.get());
        Assertions.assertEquals(2, objects.getChildren().iterator().next().y.get());
        Assertions.assertEquals(3, objects.getChildren().iterator().next().z.get());
    }

    @Test
    public void undoRemove() throws UserException {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyValueMap objects = new DummyValueMap("objects");

        DummyValueGroup objectA = new DummyValueGroup("MyObject");
        objectA.x.set(1D);
        objectA.y.set(2D);
        objectA.z.set(3D);
        root.add(objects);

        DummyHistoryManager manager = new DummyHistoryManager(root);
        root.addLogger(manager);

        objects.add(objectA);
        manager.commitUndoStep();
        objects.remove("MyObject");
        manager.commitUndoStep();

        Assertions.assertEquals(2, manager.getUndos().size());
        Assertions.assertEquals(1, manager.getUndos().peek().size());

        Assertions.assertTrue(objects.getChildren().isEmpty());
        manager.undo();

        Assertions.assertEquals(1, manager.getRedos().size());
        Assertions.assertEquals(1, manager.getRedos().peek().size());

        Assertions.assertEquals(1, objects.getChildren().size());
        Assertions.assertEquals(1, objects.getChildren().iterator().next().x.get());
        Assertions.assertEquals(2, objects.getChildren().iterator().next().y.get());
        Assertions.assertEquals(3, objects.getChildren().iterator().next().z.get());

        manager.redo();

        Assertions.assertEquals(2, manager.getUndos().size());
        Assertions.assertEquals(1, manager.getUndos().peek().size());

        Assertions.assertTrue(objects.getChildren().isEmpty());

        manager.setRecording(false);
        objects.add(new DummyValueGroup("MyObject"));
        manager.setRecording(true);

        manager.undo();

        Assertions.assertEquals(1, manager.getRedos().size());
        Assertions.assertEquals(1, manager.getRedos().peek().size());

        BaseStructure child = objects.getChildByValuePath("[\"MyObject.001\"]");
        Assertions.assertNotNull(child);
        Assertions.assertEquals(1, ((DummyValueGroup)child).x.get());
        Assertions.assertEquals(2, ((DummyValueGroup)child).y.get());
        Assertions.assertEquals(3, ((DummyValueGroup)child).z.get());
    }

    @Test
    public void undoRemoveWithPointer() throws UserException {
        DummyBaseValueHierarchy root = new DummyBaseValueHierarchy("root", ValuePath.Format.PATH);
        DummyValueMap objects = new DummyValueMap("objects");
        ValuePointer<DummyValueGroup> pointer = new ValuePointer<DummyValueGroup>("pointer", DummyValueGroup.class, root);

        DummyValueGroup objectA = new DummyValueGroup("MyObject");
        objectA.x.set(1D);
        objectA.y.set(2D);
        objectA.z.set(3D);
        root.add(objects, pointer);

        DummyHistoryManager manager = new DummyHistoryManager(root);
        root.addLogger(manager);

        objects.add(objectA);
        manager.commitUndoStep();
        manager.setRecording(false);
        pointer.set(objectA);
        manager.setRecording(true);

        Assertions.assertEquals(objectA, pointer.get());

        objects.remove(objectA);
        manager.commitUndoStep();

        Assertions.assertEquals(0, objects.getChildren().size());
        Assertions.assertNull(pointer.get());

        manager.undo();
        Assertions.assertEquals(1, objects.getChildren().size());
        Assertions.assertEquals(objects.getChildren().iterator().next(), pointer.get());
    }
}
