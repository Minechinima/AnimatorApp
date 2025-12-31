package com.minechinima.operator.history;

import com.minechinima.controller.Transaction;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.base.dummies.DummyDoubleValueMap;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.logging.operations.MapValueAdd;
import com.minechinima.model.logging.operations.ValueChange;
import com.minechinima.model.values.DoubleValue;
import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.UIOperatorContext;
import com.minechinima.operator.history.strategies.ValueChangeMerge;
import com.minechinima.operator.history.strategies.ValueChangeParallelCheck;
import com.minechinima.utils.BooleanOperand;
import com.minechinima.utils.ReflectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Stack;

public class HistoryManagerTest {
    public static CompressionStrategyRegistry getCompressionStrategyRegistry() {
        CompressionStrategyRegistry compressionStrategyRegistry = new CompressionStrategyRegistry();
        compressionStrategyRegistry.registerMerge(ValueChange.class, ValueChange.class, new ValueChangeMerge());
        compressionStrategyRegistry.registerParallelCheck(ValueChange.class, IDataOperation.class, new ValueChangeParallelCheck());
        return compressionStrategyRegistry;
    }

    @Test
    public void testCompressLogOneOperationMerging() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
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
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
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
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
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
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
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

    @Test
    public void testMergeConsecutiveNoOverlapSimple() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(3);

        historyManager.pushUndo(tests[0], marker1);
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2], marker1);
        historyManager.mergeUndoSteps("test", marker1);
        Assertions.assertEquals(1, historyManager.undos.size());
        Assertions.assertEquals("test", historyManager.undos.peek().getStepTitle());
    }

    public static Transaction[] getCommittedTransactionsForUndo(int num) {
        Transaction[] transactions = new Transaction[num];
        for (int i = 0; i < num; i++) {
            transactions[i] = new Transaction(String.valueOf(i));
            ReflectionUtils.invokePrivateMethod(transactions[i], "committedState");
        }
        return transactions;
    }

    @Test
    public void testMergeConsecutiveNoOverlapSimple2() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(4);

        historyManager.pushUndo(tests[0]);
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2], marker1);
        historyManager.pushUndo(tests[3]);

        historyManager.mergeUndoSteps("test", marker1);
        Assertions.assertEquals(3, historyManager.undos.size());
        Assertions.assertEquals("test", historyManager.undos.get(1).getStepTitle());
    }

    @Test
    public void testMergeNotConsecutiveNoOverlapSimple() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(5);

        historyManager.pushUndo(tests[0], marker1); //these should be merged too
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2]);
        historyManager.pushUndo(tests[3], marker1);
        historyManager.pushUndo(tests[4], marker1);

        historyManager.mergeUndoSteps("test", marker1);
        Assertions.assertEquals(3, historyManager.undos.size());
        Assertions.assertEquals("test", historyManager.undos.get(0).getStepTitle());
        Assertions.assertEquals("test", historyManager.undos.get(2).getStepTitle());
    }

    @Test
    public void testMergeOverlapping() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        HistoryManager.HistoryMarker marker2 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 2;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(5);

        historyManager.pushUndo(tests[0], marker1); //these shouldn't be merged
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2], marker1, marker2);
        historyManager.pushUndo(tests[3], marker1, marker2);
        historyManager.pushUndo(tests[4], marker1, marker2);

        historyManager.mergeUndoSteps("test1", marker2);
        historyManager.mergeUndoSteps("test", marker1);
        Assertions.assertEquals(1, historyManager.undos.size());
        Assertions.assertEquals("test", historyManager.undos.get(0).getStepTitle());
    }

    @Test
    public void testMergeOverlapping2() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        HistoryManager.HistoryMarker marker2 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 2;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(5);

        historyManager.pushUndo(tests[0], marker1); //these shouldn't be merged
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2], marker1, marker2);
        historyManager.pushUndo(tests[3], marker1, marker2);
        historyManager.pushUndo(tests[4], marker1, marker2);

        /* test different order of merging - first merge the overlapping root thing */
        historyManager.mergeUndoSteps("test", marker1);
        historyManager.mergeUndoSteps("test1", marker2);

        Assertions.assertEquals(1, historyManager.undos.size());
        Assertions.assertEquals("test1", historyManager.undos.get(0).getStepTitle());
    }

    @Test
    public void testMergeOverlappingDifferentMarkers() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        HistoryManager.HistoryMarker marker2 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 2;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(5);

        historyManager.pushUndo(tests[0], marker1); //these shouldn't be merged
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2], marker1, marker2);
        historyManager.pushUndo(tests[3], marker2); //TODO is it a goood idea that this is still merged?
        historyManager.pushUndo(tests[4], marker1);

        /* test different order of merging - first merge the overlapping root thing */
        historyManager.mergeUndoSteps("test1", marker2);
        historyManager.mergeUndoSteps("test", marker1);

        Assertions.assertEquals(1, historyManager.undos.size());
        Assertions.assertEquals("test", historyManager.undos.get(0).getStepTitle());
    }



    @Test
    public void testPopConsecutiveUndo() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        BaseHierarchyRoot relative = new DummyBaseHierarchyRoot("root");
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        HistoryManager.HistoryMarker marker2 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 2;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(6);

        historyManager.pushUndo(tests[0]);
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2], marker1);
        historyManager.pushUndo(tests[3], marker1);
        historyManager.pushUndo(tests[4], marker1);
        historyManager.pushUndo(tests[5], marker2);

        HistoryManager.UndoRedoStep popped = historyManager.popConsecutiveUndoByMarker(relative, null, null, BooleanOperand.AND, marker1);
        Assertions.assertEquals(4, popped.historyStep.getSteps().size());
        Assertions.assertEquals("1", popped.historyStep.getSteps().get(0).stepTitle());
        Assertions.assertEquals("2", popped.historyStep.getSteps().get(1).stepTitle());
        Assertions.assertEquals("3", popped.historyStep.getSteps().get(2).stepTitle());
        Assertions.assertEquals("4", popped.historyStep.getSteps().get(3).stepTitle());
    }

    @Test
    public void testPopAllUndo() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        BaseHierarchyRoot relative = new DummyBaseHierarchyRoot("root");
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        HistoryManager.HistoryMarker marker2 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 2;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(6);

        historyManager.pushUndo(tests[0]);
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2], marker1);
        historyManager.pushUndo(tests[3]);
        historyManager.pushUndo(tests[4], marker1);
        historyManager.pushUndo(tests[5], marker1);

        HistoryManager.UndoRedoStep popped = historyManager.popAllUndoByMarker(relative, null, null, BooleanOperand.AND, marker1);
        Assertions.assertEquals(4, popped.historyStep.getSteps().size());
        Assertions.assertEquals("1", popped.historyStep.getSteps().get(0).stepTitle());
        Assertions.assertEquals("2", popped.historyStep.getSteps().get(1).stepTitle());
        Assertions.assertEquals("4", popped.historyStep.getSteps().get(2).stepTitle());
        Assertions.assertEquals("5", popped.historyStep.getSteps().get(3).stepTitle());
    }

    @Test
    public void testMatchesMarkerXOR() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        BaseHierarchyRoot relative = new DummyBaseHierarchyRoot("root");
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        HistoryManager.HistoryMarker marker2 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 2;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(6);

        historyManager.pushUndo(tests[0]);
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2], marker1);
        historyManager.pushUndo(tests[3]);
        historyManager.pushUndo(tests[4], marker1);
        historyManager.pushUndo(tests[5], marker1, marker2);

        HistoryManager.UndoRedoStep popped = historyManager.popAllUndoByMarker(relative, null, null, BooleanOperand.XOR, marker1, marker2);
        Assertions.assertEquals(3, popped.historyStep.getSteps().size());
        Assertions.assertEquals("1", popped.historyStep.getSteps().get(0).stepTitle());
        Assertions.assertEquals("2", popped.historyStep.getSteps().get(1).stepTitle());
        Assertions.assertEquals("4", popped.historyStep.getSteps().get(2).stepTitle());
    }

    @Test
    public void testMatchesMarkerOR() {
        HistoryManager historyManager = new HistoryManager(getCompressionStrategyRegistry());
        BaseHierarchyRoot relative = new DummyBaseHierarchyRoot("root");
        HistoryManager.HistoryMarker marker1 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        HistoryManager.HistoryMarker marker2 = new HistoryManager.HistoryMarker() {
            @Override
            public int hashCode() {
                return 2;
            }
        };
        Transaction[] tests = getCommittedTransactionsForUndo(6);

        historyManager.pushUndo(tests[0]);
        historyManager.pushUndo(tests[1], marker1);
        historyManager.pushUndo(tests[2], marker2);
        historyManager.pushUndo(tests[3]);
        historyManager.pushUndo(tests[4], marker1);
        historyManager.pushUndo(tests[5], marker1, marker2);

        HistoryManager.UndoRedoStep popped = historyManager.popAllUndoByMarker(relative, null, null, BooleanOperand.OR, marker2);
        Assertions.assertEquals(2, popped.historyStep.getSteps().size());
        Assertions.assertEquals("2", popped.historyStep.getSteps().get(0).stepTitle());
        Assertions.assertEquals("5", popped.historyStep.getSteps().get(1).stepTitle());
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
