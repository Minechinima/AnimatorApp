package com.minechinima.operator.history;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.base.dummies.DummyDoubleValueMap;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.logging.operations.MapValueAdd;
import com.minechinima.model.logging.operations.ValueChange;
import com.minechinima.model.values.DoubleValue;
import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.history.strategies.ValueChangeMerge;
import com.minechinima.operator.history.strategies.ValueChangeParallelCheck;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Stack;

public class HistoryManagerTest {
    @Test
    public void testCompressLogOneOperationMerging() {
        CompressionStrategyRegistry compressionStrategyRegistry = new CompressionStrategyRegistry();
        compressionStrategyRegistry.registerMerge(ValueChange.class, ValueChange.class, new ValueChangeMerge());
        compressionStrategyRegistry.registerParallelCheck(ValueChange.class, IDataOperation.class, new ValueChangeParallelCheck());

        HistoryManager historyManager = new HistoryManager(compressionStrategyRegistry);
        FloatValue slider = new FloatValue("a");
        DataOperationLog log = new DataOperationLog();
        slider.getDataOperationBus().registerLog(log);
        slider.set(1F);
        slider.set(2F);
        slider.set(3F);
        slider.set(4F);
        log.close();
        Stack<DataOperationLog.Entry> compressed = historyManager.compressLog(log.getLog());
        Assertions.assertEquals(1, compressed.size());
    }

    @Test
    public void testCompressLogMergingTwo() {
        CompressionStrategyRegistry compressionStrategyRegistry = new CompressionStrategyRegistry();
        compressionStrategyRegistry.registerMerge(ValueChange.class, ValueChange.class, new ValueChangeMerge());
        compressionStrategyRegistry.registerParallelCheck(ValueChange.class, IDataOperation.class, new ValueChangeParallelCheck());

        HistoryManager historyManager = new HistoryManager(compressionStrategyRegistry);
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root");
        FloatValue slider = new FloatValue("a");
        FloatValue slider2 = new FloatValue("b");
        root._addChild("slider1", slider);
        root._addChild("slider2", slider2);
        root.initChildren();


        DataOperationLog log = new DataOperationLog();
        root.getDataOperationBus().registerLog(log);

        slider.set(1F);
        slider.set(2F);
        slider.set(3F);
        slider.set(4F);

        slider2.set(7F);
        slider2.set(8F);
        slider2.set(9F);
        slider2.set(10F);

        log.close();
        Assertions.assertEquals(2, historyManager.compressLog(log.getLog()).size());
    }

    @Test
    public void testCompressLogParallelity() {
        CompressionStrategyRegistry compressionStrategyRegistry = new CompressionStrategyRegistry();
        compressionStrategyRegistry.registerMerge(ValueChange.class, ValueChange.class, new ValueChangeMerge());
        compressionStrategyRegistry.registerParallelCheck(ValueChange.class, IDataOperation.class, new ValueChangeParallelCheck());

        HistoryManager historyManager = new HistoryManager(compressionStrategyRegistry);
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root");
        FloatValue slider = new FloatValue("a");
        FloatValue slider2 = new FloatValue("b");
        root._addChild("slider1", slider);
        root._addChild("slider2", slider2);
        root.initChildren();


        DataOperationLog log = new DataOperationLog();
        root.getDataOperationBus().registerLog(log);

        slider.set(1F);
        slider2.set(7F);

        slider.set(2F);
        slider2.set(8F);

        slider.set(3F);
        slider2.set(9F);

        slider.set(4F);
        slider2.set(9F);

        log.close();
        Stack<DataOperationLog.Entry> compressed = historyManager.compressLog(log.getLog());
        Assertions.assertEquals(2, compressed.size());

        BaseType newValue0 = getPrivateFieldValue(compressed.get(0).getOperation(), "newValue", BaseType.class);
        BaseType oldValue0 = getPrivateFieldValue(compressed.get(0).getOperation(), "oldValue", BaseType.class);
        Assertions.assertEquals(0, oldValue0.asFloatType().getValue());
        Assertions.assertEquals(4, newValue0.asFloatType().getValue());

        BaseType newValue1 = getPrivateFieldValue(compressed.get(1).getOperation(), "newValue", BaseType.class);
        BaseType oldValue1 = getPrivateFieldValue(compressed.get(1).getOperation(), "oldValue", BaseType.class);
        Assertions.assertEquals(0, oldValue1.asFloatType().getValue());
        Assertions.assertEquals(9, newValue1.asFloatType().getValue());
    }

    @Test
    public void testCompressLogParallelityWithInterrupt() {
        CompressionStrategyRegistry compressionStrategyRegistry = new CompressionStrategyRegistry();
        compressionStrategyRegistry.registerMerge(ValueChange.class, ValueChange.class, new ValueChangeMerge());
        compressionStrategyRegistry.registerParallelCheck(ValueChange.class, IDataOperation.class, new ValueChangeParallelCheck());

        HistoryManager historyManager = new HistoryManager(compressionStrategyRegistry);
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root");
        FloatValue slider = new FloatValue("a");
        FloatValue slider2 = new FloatValue("b");
        DummyDoubleValueMap map = new DummyDoubleValueMap("map");
        root._addChild("slider1", slider);
        root._addChild("slider2", slider2);
        root._addChild("map", map);
        root.initChildren();

        DataOperationLog log = new DataOperationLog();
        root.getDataOperationBus().registerLog(log);

        slider.set(1F);
        slider2.set(7F);

        slider.set(2F);
        slider2.set(8F);

        slider.set(3F);
        slider2.set(9F);

        slider.set(4F);
        slider2.set(9F);

        map.add(new DoubleValue("c"));

        slider.set(5F);
        slider2.set(10F);

        slider.set(6F);
        slider2.set(11F);

        log.close();
        Stack<DataOperationLog.Entry> compressed = historyManager.compressLog(log.getLog());
        Assertions.assertEquals(5, compressed.size());

        BaseType newValue0 = getPrivateFieldValue(compressed.get(0).getOperation(), "newValue", BaseType.class);
        BaseType oldValue0 = getPrivateFieldValue(compressed.get(0).getOperation(), "oldValue", BaseType.class);
        Assertions.assertEquals(0, oldValue0.asFloatType().getValue());
        Assertions.assertEquals(4, newValue0.asFloatType().getValue());

        BaseType newValue1 = getPrivateFieldValue(compressed.get(1).getOperation(), "newValue", BaseType.class);
        BaseType oldValue1 = getPrivateFieldValue(compressed.get(1).getOperation(), "oldValue", BaseType.class);
        Assertions.assertEquals(0, oldValue1.asFloatType().getValue());
        Assertions.assertEquals(9, newValue1.asFloatType().getValue());

        Assertions.assertInstanceOf(MapValueAdd.class, compressed.get(2).getOperation());

        BaseType newValue3 = getPrivateFieldValue(compressed.get(3).getOperation(), "newValue", BaseType.class);
        BaseType oldValue3 = getPrivateFieldValue(compressed.get(3).getOperation(), "oldValue", BaseType.class);
        Assertions.assertEquals(4, oldValue3.asFloatType().getValue());
        Assertions.assertEquals(6, newValue3.asFloatType().getValue());

        BaseType newValue4 = getPrivateFieldValue(compressed.get(4).getOperation(), "newValue", BaseType.class);
        BaseType oldValue4 = getPrivateFieldValue(compressed.get(4).getOperation(), "oldValue", BaseType.class);
        Assertions.assertEquals(9, oldValue4.asFloatType().getValue());
        Assertions.assertEquals(11, newValue4.asFloatType().getValue());
    }

    public static <V> V getPrivateFieldValue(Object target, String fieldName, Class<V> fieldType) {
        if (target == null || fieldName == null) {
            throw new IllegalArgumentException("Target object and field name must not be null.");
        }

        Class<?> clazz = target.getClass();

        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (V) field.get(target);
            } catch (NoSuchFieldException e) {
                // Try superclass
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not access the field: " + fieldName, e);
            }
        }

        throw new RuntimeException("Field '" + fieldName + "' not found in class hierarchy.");
    }
}
