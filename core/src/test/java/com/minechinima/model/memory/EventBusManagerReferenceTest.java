package com.minechinima.model.memory;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.events.bus.EventBusManager;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.AbstractEvent;
import com.minechinima.model.values.DoubleValue;

import java.util.HashMap;
import java.util.Map;

public class EventBusManagerReferenceTest extends ReferencesImplTest<EventBusManager> {
    public enum Cases {
        REGISTER_BUS,
        SUBSCRIBE,
        SUBSCRIBE_AND_IS_BUS
    }
    public static Map<Enum, BaseStructure> getTestCases() {
        Map<Enum, BaseStructure> cases = new HashMap<>();
        cases.put(Cases.REGISTER_BUS, new DoubleValue(""));
        cases.put(Cases.SUBSCRIBE, new EventSubscriber());
        cases.put(Cases.SUBSCRIBE_AND_IS_BUS, new EventSubscriber());

        return cases;
    }

    public EventBusManagerReferenceTest() {
        super(environment -> new EventBusManager(environment.treeManager, environment.registry), getTestCases());
    }

    @Override
    public void referenceNodeTestCase(ReferenceManagerTest.TestEnvironment environment,
                                      EventBusManager implInstance,
                                      Map.Entry<Enum, BaseStructure> testCase) {
        BaseStructure testReference = testCase.getValue();
        if (testCase.getKey().equals(Cases.REGISTER_BUS)) {
            EventSubscriber testSubscriber = new EventSubscriber();
            environment.map.add(testSubscriber);
            implInstance.subscribe(testReference.getEventBus(), testSubscriber);
        } else if (testCase.getKey().equals(Cases.SUBSCRIBE_AND_IS_BUS)) {
            implInstance.subscribe(testReference.getEventBus(), testReference);
        } else if (testCase.getKey().equals(Cases.SUBSCRIBE)) {
            DoubleValue eventBusProvider = new DoubleValue(String.valueOf(Math.random() * 10000000));
            environment.map.add(eventBusProvider);
            implInstance.subscribe(eventBusProvider.getEventBus(), testReference);
        }
    }

    public static class EventSubscriber extends DoubleValue {
        public EventSubscriber() {
            super(String.valueOf(Math.random() * 10000), 0, 0, 0);
        }

        @SubscribeEvent(type = SubscribeEvent.Type.ALL)
        public void onEventAll(AbstractEvent event) {
        }
    }
}
