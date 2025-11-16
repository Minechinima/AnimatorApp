package com.minechinima.model.logging.undo;

import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.datainterfaces.IValueList;

import com.minechinima.model.logging.datainterfaces.ListElement;
import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.values.lists.ValuePointerList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ListUndo {
    public static Stream<Arguments> provideAddLists() {
        BaseHierarchyRoot root = new DummyBaseHierarchyRoot("");
        List<Arguments> args = new ArrayList<>();

        /* add */
        args.add(Arguments.of(new ValuePointerList<>("", BaseStructure.class, root, true, true),
                (Function<ValuePointerList<BaseStructure>, ValuePointer<BaseStructure>>) list -> {
                    ValuePointer<BaseStructure> ptr = new ValuePointer<>("", BaseStructure.class, root);
                    list.add(ptr);
                    return ptr;
        }));

        /* add at index */
        ValuePointerList<BaseStructure> list = new ValuePointerList<>("", BaseStructure.class, root, true, true);
        list.add(new ValuePointer<>("", BaseStructure.class, root));
        list.add(new ValuePointer<>("", BaseStructure.class, root));
        list.add(new ValuePointer<>("", BaseStructure.class, root));
        args.add(Arguments.of(list,
                (Function<ValuePointerList<BaseStructure>, ValuePointer<BaseStructure>>) list0 -> {
                    ValuePointer<BaseStructure> ptr = new ValuePointer<>("", BaseStructure.class, root);
                    list0.add(1, ptr);
                    return ptr;
                }));

        /* addListElement at index */
        ValuePointerList<BaseStructure> list1 = new ValuePointerList<>("", BaseStructure.class, root, true, true);
        list1.add(new ValuePointer<>("", BaseStructure.class, root));
        list1.add(new ValuePointer<>("", BaseStructure.class, root));
        list1.add(new ValuePointer<>("", BaseStructure.class, root));
        args.add(Arguments.of(list1,
                (Function<ValuePointerList<BaseStructure>, ValuePointer<BaseStructure>>) list0 -> {
                    ValuePointer<BaseStructure> ptr = new ValuePointer<>("", BaseStructure.class, root);
                    list0.addListElement(1, new ListElement<>(UUID.randomUUID(), ptr));
                    return ptr;
                }));

        /* addChild */
        ValuePointerList<BaseStructure> list2 = new ValuePointerList<>("", BaseStructure.class, root, true, true);
        list2.add(new ValuePointer<>("", BaseStructure.class, root));
        list2.add(new ValuePointer<>("", BaseStructure.class, root));
        list2.add(new ValuePointer<>("", BaseStructure.class, root));
        args.add(Arguments.of(list2,
                (Function<ValuePointerList<BaseStructure>, ValuePointer<BaseStructure>>) list0 -> {
                    ValuePointer<BaseStructure> ptr = new ValuePointer<>("", BaseStructure.class, root);
                    list0.addChild(ptr);
                    return ptr;
                }));

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("provideAddLists")
    public <V, L extends BaseStructure & IValueList<V>> void undoAdd(L list, Function<L, V> addElement) {
        DataOperationLog log = new DataOperationLog();
        list.getDataOperationBus().registerLog(log);
        int prevSize = list.size();

        V value = testAddSuccess(list, addElement);
        log.close();

        assertNotEquals(0, log.getLog().size());
        UUID uuid = list.listUUIDOf(value);
        undoOperations(list, log);
        assertEquals(prevSize, list.size());
        assertFalse(list.containsListUUID(uuid));
    }

    @ParameterizedTest
    @MethodSource("provideAddLists")
    public <V, L extends BaseStructure & IValueList<V>> void undoAddAlreadyRemoved(L list, Function<L, V> addElement) {
        DataOperationLog log = new DataOperationLog();
        list.getDataOperationBus().registerLog(log);
        int prevSize = list.size();

        V value = testAddSuccess(list, addElement);
        log.close();
        testRemoveSuccess(list, value);

        assertEquals(1, log.getLog().size());
        undoOperations(list, log);
        assertEquals(prevSize, list.size());
        assertFalse(list.contains(value));
    }

    private static <V, L extends IValueList<V>> V testAddSuccess(L list, Function<L, V> addElement) {
        int prevSize = list.size();
        V value = addElement.apply(list);
        assertEquals(prevSize + 1, list.size());
        UUID listUUID;
        assertNotNull(listUUID = list.listUUIDOf(value));
        return value;
    }

    private static <V, L extends IValueList<V>> void testRemoveSuccess(L list, V e) {
        int prevSize = list.size();
        UUID uuid = list.listUUIDOf(e);
        assertNotNull(uuid);
        list.removeByListUUID(uuid);
        assertEquals(prevSize - 1, list.size());
        assertNull(list.listUUIDOf(e));
        assertFalse(list.contains(e));
    }

    private static void undoOperations(BaseStructure context, DataOperationLog log) {
        Stack<DataOperationLog.Entry> entries = log.getLog();
        while (!entries.isEmpty()) {
            DataOperationLog.Entry entry = entries.pop();
            IDataOperation op = entry.getOperation();
            if (!op.isCorrectType(context)) {
                fail();
            }

            try {
                op.undo(context);
            } catch (UserException e) {
                fail();
            }
        }
    }
}
