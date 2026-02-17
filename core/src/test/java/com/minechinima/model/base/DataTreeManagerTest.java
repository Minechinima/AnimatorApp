package com.minechinima.model.base;

import com.minechinima.controller.ControllerFacade;
import com.minechinima.controller.ControllerManager;
import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.base.dummies.DummyBaseImmutableHierarchy;
import com.minechinima.model.base.dummies.DummyBaseMutableHierarchy;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

public class DataTreeManagerTest {
    @Test
    public void testAddingTreeManagerToChildren() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("test");
        DataTreeManager treeManager = new DataTreeManager(root, null);

        DummyBaseMutableHierarchy test = new DummyBaseMutableHierarchy("map", ValuePath.Format.MAP);
        root.addChild("map", test);
        root.initChildren();

        DummyBaseImmutableHierarchy subGroup = new DummyBaseImmutableHierarchy("subGroup", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy group = new DummyBaseImmutableHierarchy("group", ValuePath.Format.PATH);
        group.addChild("a", new DoubleValue("a"));
        group.addChild("b", new DoubleValue("b"));
        group.addChild("subGroup", subGroup);

        group.initChildren();

        test.add(group);

        this.recursiveTreeCheck(group, node -> Assertions.assertEquals(treeManager, node.dataTreeManager));
    }

    @Test
    public void testRemovingTreeManagerToChildren() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("test");
        DataTreeManager treeManager = new DataTreeManager(root, null);

        DummyBaseMutableHierarchy test = new DummyBaseMutableHierarchy("map", ValuePath.Format.MAP);
        root.addChild("map", test);
        root.initChildren();

        DummyBaseImmutableHierarchy subGroup = new DummyBaseImmutableHierarchy("subGroup", ValuePath.Format.PATH);
        DummyBaseImmutableHierarchy group = new DummyBaseImmutableHierarchy("group", ValuePath.Format.PATH);
        group.addChild("a", new DoubleValue("a"));
        group.addChild("b", new DoubleValue("b"));
        group.addChild("subGroup", subGroup);

        group.initChildren();

        test.add(group);
        test.removeChild(group);

        this.recursiveTreeCheck(group, node -> Assertions.assertNull(node.dataTreeManager));
    }

    private void recursiveTreeCheck(BaseStructure node, Consumer<BaseStructure> consumer) {
        consumer.accept(node);
        if (node instanceof BaseValueHierarchy<?> hierarchy) {
            hierarchy.getChildren().forEach(child -> recursiveTreeCheck(child, consumer));
        }
    }
}
