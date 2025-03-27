package com.minechinima.model.events;

import com.minechinima.model.DataTreeManager;
import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.changes.ChangeEvent;
import com.minechinima.model.events.types.changes.IdentifierChangeEvent;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.values.FloatValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventSubscriptionTest {
    @Test
    public void testEventDispatch() {
        DummyBaseHierarchyRoot root = new DummyBaseHierarchyRoot("root");
        DummyEventDispatcher val = new DummyEventDispatcher("val");
        root._addChild("test", val);
        root.initChildren();

        EventTypeRegistry registry = new EventTypeRegistry();
        EventTypeRegistry.registerDefaultEventTypes(registry);

        DataTreeManager tree = new DataTreeManager<>(root, registry);
        EventSubscriber tester = new EventSubscriber();


        val.getEventBus().subscribe(tester);

        val._triggerEvent(new ValueChangeEvent(val, val.valueToData(), val.valueToData()));
        Assertions.assertEquals(1, tester.onEventAll);
        Assertions.assertEquals(0, tester.onEventInChildren);
        Assertions.assertEquals(1, tester.onEventOnNode);
        Assertions.assertEquals(1, tester.onValueChangeEventOnNode);
        Assertions.assertEquals(1, tester.onChangeEventOnNode);

        tree.getRoot().getEventBus().subscribe(tester);

        val._triggerEvent(new IdentifierChangeEvent(val, "a", "b"));
        Assertions.assertEquals(3, tester.onEventAll);
        Assertions.assertEquals(1, tester.onEventInChildren);
        Assertions.assertEquals(2, tester.onEventOnNode);
        Assertions.assertEquals(1, tester.onValueChangeEventOnNode);
        Assertions.assertEquals(2, tester.onChangeEventOnNode);
    }

    public static class EventSubscriber {
        private int onEventAll = 0;
        private int onEventInChildren = 0;
        private int onEventOnNode = 0;
        private int onValueChangeEventOnNode = 0;
        private int onChangeEventOnNode = 0;

        public EventSubscriber() {
        }

        @SubscribeEvent(type = SubscribeEvent.Type.ALL)
        public void onEventAll(Event event) {
            onEventAll++;
        }

        @SubscribeEvent(type = SubscribeEvent.Type.IN_CHILDREN)
        public void onEventInChildren(Event event) {
            onEventInChildren++;
        }

        @SubscribeEvent(type = SubscribeEvent.Type.ON_NODE)
        public void onEventOnNode(Event event) {
            onEventOnNode++;
        }

        @SubscribeEvent(type = SubscribeEvent.Type.ON_NODE)
        public void onValueChangeEventOnNode(ValueChangeEvent event) {
            onValueChangeEventOnNode++;
        }

        @SubscribeEvent(type = SubscribeEvent.Type.ON_NODE)
        public void onChangeEventOnNode(ChangeEvent event) {
            onChangeEventOnNode++;
        }
    }

    private class DummyEventDispatcher extends FloatValue {

        public DummyEventDispatcher(String id) {
            super(id, 0);
        }

        public void _triggerEvent(Event event) {
            super.triggerEvent(event);
        }
    }
}
