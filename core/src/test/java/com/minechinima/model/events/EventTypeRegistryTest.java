package com.minechinima.model.events;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.events.types.AbstractEvent;
import com.minechinima.model.events.types.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class EventTypeRegistryTest {
    @Test
    public void compileFullEventHierarchy() {
        /* simple test whether a normal, without missings or any other weird stuff, hierarchy works */
        EventTypeRegistry registry = new EventTypeRegistry();
        registry.addEventClass(AbstractEvent.class);
        registry.addEventClass(EventSub.class);
        registry.addEventClass(EventSub2.class);
        registry.addEventClass(EventSubSub.class);
        registry.addEventClass(EventSubSub2.class);
        registry.addEventClass(EventSubSubSub.class);
        registry.addEventClass(EventSubSubSub2.class);

        EventTypeNode<AbstractEvent> root = registry.compileEventHierarchy();

        Assertions.assertEquals(AbstractEvent.class, root.eventType);
        Assertions.assertEquals(2, root.immediateSubTypes.size());

        Set<Class<? extends AbstractEvent>> layer1 = Set.of(EventSub.class, EventSub2.class);
        Set<Class<? extends AbstractEvent>> layer2 = Set.of(EventSubSub.class, EventSubSub2.class);
        Set<Class<? extends AbstractEvent>> layer3 = Set.of(EventSubSubSub.class, EventSubSubSub2.class);

        EventTypeNode<AbstractEvent> current = root;
    }

    @Test
    public void compileIncompleteEventHierarchy() {
        /* simple test whether a normal, without missings or any other weird stuff, hierarchy works */
        EventTypeRegistry registry = new EventTypeRegistry();
        registry.addEventClass(EventSubSubSub.class);
        registry.addEventClass(EventSubSubSub2.class);

        EventTypeNode<AbstractEvent> root = registry.compileEventHierarchy();

        Assertions.assertEquals(AbstractEvent.class, root.eventType);
        Assertions.assertEquals(0, root.immediateSubTypes.size());
    }

    /* test event classes */
    public class EventSub extends Event {
        public EventSub(BaseStructure trigger) {
            super(trigger);
        }
    }

    public class EventSub2 extends Event {
        public EventSub2(BaseStructure trigger) {
            super(trigger);
        }
    }

    public class EventSubSub extends EventSub {
        public EventSubSub(BaseStructure trigger) {
            super(trigger);
        }
    }

    public class EventSubSub2 extends EventSub {
        public EventSubSub2(BaseStructure trigger) {
            super(trigger);
        }
    }

    public class EventSubSubSub extends EventSubSub {
        public EventSubSubSub(BaseStructure trigger) {
            super(trigger);
        }
    }

    public class EventSubSubSub2 extends EventSubSub {
        public EventSubSubSub2(BaseStructure trigger) {
            super(trigger);
        }
    }
}
