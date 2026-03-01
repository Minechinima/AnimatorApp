package com.minechinima.eventbus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tests for the general-purpose {@link AbstractEventBus} framework.
 * <br>
 * Uses a simple self-contained bus implementation (no BaseStructure dependency) to verify that
 * the framework handles subscription, dispatch, constraint evaluation, and event-type filtering
 * independently of any domain-specific code.
 */
public class AbstractEventBusTest {

    /* ------------------------------------------------------------------
     * Minimal domain definitions used only in this test
     * ------------------------------------------------------------------ */

    /** Base event type for the test domain. */
    static class TestEvent {
        final String name;
        TestEvent(String name) { this.name = name; }
    }

    /** A sub-event type to verify event-type filtering. */
    static class SpecialTestEvent extends TestEvent {
        SpecialTestEvent() { super("special"); }
    }

    /** Constraint context: which "channel" the event is dispatched on. */
    enum Channel { A, B }

    /**
     * Base constraint class for the test bus.
     * Implementing {@link IEventConstraint}&lt;{@link Channel}&gt; allows the test annotation to
     * expose a typed {@code Class<? extends TestConstraint>} element, so IDEs can auto-suggest
     * concrete constraint implementations.
     */
    abstract static class TestConstraint implements IEventConstraint<Channel> {}

    /** Accepts only events dispatched on channel A. */
    static class ChannelAConstraint extends TestConstraint {
        @Override
        public boolean matches(Channel channel) { return channel == Channel.A; }
    }

    /** Accepts only events dispatched on channel B. */
    static class ChannelBConstraint extends TestConstraint {
        @Override
        public boolean matches(Channel channel) { return channel == Channel.B; }
    }

    /**
     * Listener annotation for the test bus.
     * The {@code constraints} element is typed as {@code Class<? extends TestConstraint>[]},
     * which gives IDEs the information needed to auto-suggest valid constraint implementations.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Listen {
        /** Zero or more constraint classes; all must match for the listener to be invoked. */
        Class<? extends TestConstraint>[] constraints() default {};
    }

    /**
     * Concrete bus implementation that instantiates and evaluates constraint classes declared in
     * the {@link Listen} annotation.
     */
    static class TestEventBus extends AbstractEventBus<TestEvent, Channel, Listen> {
        TestEventBus() {
            super(TestEvent.class, Listen.class);
        }

        @Override
        protected boolean evaluateConstraints(Listen annotation, Channel channel) {
            @SuppressWarnings("unchecked")
            Class<? extends IEventConstraint<Channel>>[] constraints =
                    (Class<? extends IEventConstraint<Channel>>[]) annotation.constraints();
            return evaluateConstraintClasses(constraints, channel);
        }

        /** Expose dispatch so tests can call it. */
        void fire(TestEvent event, Channel channel) {
            dispatch(event, channel);
        }
    }

    /* ------------------------------------------------------------------
     * Test subscriber
     * ------------------------------------------------------------------ */

    static class TestSubscriber {
        int onAll = 0;
        int onA = 0;
        int onB = 0;
        int onSpecial = 0;

        /** No constraints → receives events from every channel. */
        @Listen
        public void onAll(TestEvent event) { onAll++; }

        @Listen(constraints = {ChannelAConstraint.class})
        public void onA(TestEvent event) { onA++; }

        @Listen(constraints = {ChannelBConstraint.class})
        public void onB(TestEvent event) { onB++; }

        /** Only receives SpecialTestEvent instances, on any channel. */
        @Listen
        public void onSpecial(SpecialTestEvent event) { onSpecial++; }
    }

    /* ------------------------------------------------------------------
     * Tests
     * ------------------------------------------------------------------ */

    private TestEventBus bus;
    private TestSubscriber subscriber;

    @BeforeEach
    void setUp() {
        bus = new TestEventBus();
        subscriber = new TestSubscriber();
        bus.subscribe(subscriber);
    }

    @Test
    void testDispatchOnChannelA() {
        bus.fire(new TestEvent("e"), Channel.A);

        Assertions.assertEquals(1, subscriber.onAll,     "onAll should receive channel A events");
        Assertions.assertEquals(1, subscriber.onA,       "onA should receive channel A events");
        Assertions.assertEquals(0, subscriber.onB,       "onB should not receive channel A events");
        Assertions.assertEquals(0, subscriber.onSpecial, "onSpecial should not fire for base TestEvent");
    }

    @Test
    void testDispatchOnChannelB() {
        bus.fire(new TestEvent("e"), Channel.B);

        Assertions.assertEquals(1, subscriber.onAll,     "onAll should receive channel B events");
        Assertions.assertEquals(0, subscriber.onA,       "onA should not receive channel B events");
        Assertions.assertEquals(1, subscriber.onB,       "onB should receive channel B events");
        Assertions.assertEquals(0, subscriber.onSpecial, "onSpecial should not fire for base TestEvent");
    }

    @Test
    void testEventTypeFiltering() {
        bus.fire(new SpecialTestEvent(), Channel.A);

        Assertions.assertEquals(1, subscriber.onAll,     "onAll should receive SpecialTestEvent");
        Assertions.assertEquals(1, subscriber.onA,       "onA should receive SpecialTestEvent on channel A");
        Assertions.assertEquals(0, subscriber.onB,       "onB should not receive SpecialTestEvent on channel A");
        Assertions.assertEquals(1, subscriber.onSpecial, "onSpecial should fire for SpecialTestEvent");
    }

    @Test
    void testUnsubscribe() {
        bus.unsubscribe(subscriber);
        bus.fire(new TestEvent("e"), Channel.A);

        Assertions.assertEquals(0, subscriber.onAll, "No events should be delivered after unsubscribe");
        Assertions.assertEquals(0, subscriber.onA,   "No events should be delivered after unsubscribe");
    }

    @Test
    void testResubscribeReplacesOldSubscription() {
        bus.subscribe(subscriber);  // re-subscribe with the same object
        bus.fire(new TestEvent("e"), Channel.A);

        /* listener should be called exactly once, not twice */
        Assertions.assertEquals(1, subscriber.onAll, "Re-subscribing the same object must not double the handlers");
        Assertions.assertEquals(1, subscriber.onA,   "Re-subscribing the same object must not double the handlers");
    }

    @Test
    void testInvalidAnnotationParameterCount() {
        Object badSubscriber = new Object() {
            @Listen
            @SuppressWarnings("unused")
            public void twoParams(TestEvent a, TestEvent b) {}
        };

        Assertions.assertThrows(IllegalArgumentException.class, () -> bus.subscribe(badSubscriber),
                "Subscribing a method with wrong parameter count must throw");
    }

    @Test
    void testInvalidAnnotationParameterType() {
        Object badSubscriber = new Object() {
            @Listen
            @SuppressWarnings("unused")
            public void wrongType(String notAnEvent) {}
        };

        Assertions.assertThrows(IllegalArgumentException.class, () -> bus.subscribe(badSubscriber),
                "Subscribing a method with incompatible parameter type must throw");
    }

    @Test
    void testMultipleConstraintsAllMustMatch() {
        /* A listener that requires BOTH ChannelAConstraint AND ChannelBConstraint.
         * Since an event is dispatched on exactly one channel, both constraints can never be
         * satisfied at the same time, so this listener must never fire. */
        class MultiConstraintSubscriber {
            int count = 0;

            @Listen(constraints = {ChannelAConstraint.class, ChannelBConstraint.class})
            public void onAandB(TestEvent event) { count++; }
        }

        MultiConstraintSubscriber sub = new MultiConstraintSubscriber();
        bus.subscribe(sub);

        bus.fire(new TestEvent("e"), Channel.A);
        Assertions.assertEquals(0, sub.count,
                "Listener with two mutually-exclusive constraints must not fire on channel A");

        bus.fire(new TestEvent("e"), Channel.B);
        Assertions.assertEquals(0, sub.count,
                "Listener with two mutually-exclusive constraints must not fire on channel B");
    }
}
