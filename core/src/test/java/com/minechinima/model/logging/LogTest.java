package com.minechinima.model.logging;

import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.base.dummies.DummyBaseMutableHierarchy;
import com.minechinima.model.base.dummies.DummyDoubleValueMap;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Stack;

public class LogTest {
    @Test
    public void testLogRelativeToEmptyPaths() {
        DataOperationLog log = new DataOperationLog();

        /* log should save and use full path with empty elements to fully compare path segments for equality */
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyDoubleValueMap map = new DummyDoubleValueMap("map");
        DoubleValue value = new DoubleValue("a");

        root._addChild("h1", h1.add(h2.add(map)));
        root.initChildren();
        map.add(value);

        h2.getLogging().registerLog(log);

        value.set(1D);
        value.set(2D);
        map.remove(value);

        log.close();

        Stack<DataOperationLog.RelativeEntry> h2Stack = log.getLogRelativeTo(h2);
        Stack<DataOperationLog.RelativeEntry> h1Stack = log.getLogRelativeTo(h1);
        Stack<DataOperationLog.RelativeEntry> rootStack = log.getLogRelativeTo(root);
        Stack<DataOperationLog.RelativeEntry> mapStack = log.getLogRelativeTo(map);
        Assertions.assertEquals(3, h1Stack.size());
        Assertions.assertEquals(3, h2Stack.size());
        Assertions.assertEquals(3, rootStack.size());
        Assertions.assertEquals(3, mapStack.size());
    }

    @Test
    public void testLogRelativeToOnSource() {
        DataOperationLog log = new DataOperationLog();

        /* log should save and use full path with empty elements to fully compare path segments for equality */
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h1 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyBaseMutableHierarchy h2 = new DummyBaseMutableHierarchy("", ValuePath.Format.PATH);
        DummyDoubleValueMap map = new DummyDoubleValueMap("map");
        DoubleValue value = new DoubleValue("a");

        root._addChild("h1", h1.add(h2.add(map)));
        root.initChildren();
        map.add(value);

        root.getLogging().registerLog(log);

        value.set(1D);
        value.set(2D);
        map.remove(value);

        log.close();

        Stack<DataOperationLog.RelativeEntry> mapStack = log.getLogRelativeTo(map);
        Assertions.assertEquals(3, mapStack.size());
        Assertions.assertNull(mapStack.pop().path);
        Assertions.assertEquals("[\"a\"]", mapStack.pop().path.toValuePath());
        Assertions.assertEquals("[\"a\"]", mapStack.pop().path.toValuePath());
    }
}
