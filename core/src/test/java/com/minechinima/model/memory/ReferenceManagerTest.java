package com.minechinima.model.memory;

import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.base.dummies.DummyBaseMap;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.events.EventTypeRegistryTest;
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
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("r");
        DummyBaseMap map = new DummyBaseMap("map");
        root._addChild("var", map);
        root.initChildren();

        EventTypeRegistry registry = new EventTypeRegistry();
        registry.addEventClass(RemoveFromTreeEvent.class);
        registry.addEventClass(AddToTreeEvent.class);
        registry.addEventClass(ParentChangeEvent.class);
        registry.addEventClass(ValueAddEvent.class);
        registry.addEventClass(ValueRemoveEvent.class);
        ReferenceManager manager = new ReferenceManager(root);
        EventBusManager busManager = new EventBusManager(manager, root, registry);
        manager.initEventSubscription(busManager);

        SimpleReference reference = new SimpleReference(manager);
        map.add(new DoubleValue("aa"));
        reference.set(map.get("aa"));
        Assertions.assertNotNull(reference.get());
        map.remove("aa");
        Assertions.assertNull(reference.get());
    }
}
