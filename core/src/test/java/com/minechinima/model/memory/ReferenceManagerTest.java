package com.minechinima.model.memory;

import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.base.dummies.DummyDoubleValueMap;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.events.bus.EventBusManager;
import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.ParentChangeEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.events.types.changes.ValueAddEvent;
import com.minechinima.model.events.types.changes.ValueRemoveEvent;
import com.minechinima.model.values.DoubleValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReferenceManagerTest {
    @Test
    public void clearReferenceTest() {
        TestEnvironment test = new TestEnvironment();

        SimpleReference reference = new SimpleReference(test.manager);
        test.map.add(new DoubleValue("aa"));
        reference.set(test.map.get("aa"));
        Assertions.assertNotNull(reference.get());
        test.map.remove("aa");
        Assertions.assertNull(reference.get());
    }

    @Test
    public void reapplyCachedReferencesTest() {
        TestEnvironment test = new TestEnvironment();

        DoubleValue val = new DoubleValue("aa");
        SimpleReference reference = new SimpleReference(test.manager);
        test.map.add(val);
        reference.set(val);
        Assertions.assertNotNull(reference.get());

        test.manager.cacheClearedReferences();
        test.map.remove(val);
        Assertions.assertNull(reference.get());

        test.map.add(val);
        test.manager.reapplyCachedClearedReferences();
        Assertions.assertNotNull(reference.get());

        test.map.remove(val);
        Assertions.assertNull(reference.get());
    }

    @Test
    public void finalzieCachedReferencesTest() {
        TestEnvironment test = new TestEnvironment();

        DoubleValue val = new DoubleValue("aa");
        SimpleReference reference = new SimpleReference(test.manager);
        test.map.add(val);
        reference.set(val);
        Assertions.assertNotNull(reference.get());

        test.manager.cacheClearedReferences();
        test.map.remove(val);
        Assertions.assertNull(reference.get());

        test.map.add(val);
        test.manager.finalizeCachedClearedReferences();
        test.manager.reapplyCachedClearedReferences();
        Assertions.assertNull(reference.get());
    }

    public class TestEnvironment {
        DummyBaseHierarchyRoot root;
        DummyDoubleValueMap map;
        ReferenceManager manager;
        EventBusManager busManager;
        public TestEnvironment() {
            root = new DummyBaseHierarchyRoot("r");
            map = new DummyDoubleValueMap("map");
            root._addChild("var", map);
            root.initChildren();

            EventTypeRegistry registry = new EventTypeRegistry();
            registry.addEventClass(RemoveFromTreeEvent.class);
            registry.addEventClass(AddToTreeEvent.class);
            registry.addEventClass(ParentChangeEvent.class);
            registry.addEventClass(ValueAddEvent.class);
            registry.addEventClass(ValueRemoveEvent.class);
            manager = new ReferenceManager(root);
            busManager = new EventBusManager(manager, root, registry);
            manager.initEventSubscription(busManager);
        }
    }
}
