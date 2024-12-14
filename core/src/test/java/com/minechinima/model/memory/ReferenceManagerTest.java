package com.minechinima.model.memory;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.model.base.dummies.DummyBaseHierarchyRoot;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.events.bus.EventBusManager;
import com.minechinima.model.values.DoubleValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class ReferenceManagerTest {
    public static Stream<Arguments> getReferenceImplementations() {
        return Stream.of(
                Arguments.of(new SimpleReferenceTestImpl()),
                Arguments.of(new EventBusManagerReferenceTest())
        );
    }

    @ParameterizedTest()
    @MethodSource("getReferenceImplementations")
    public <T extends BaseStructureReferences> void clearReferenceTest(ReferencesImplTest<T> testImpl) {
        TestEnvironment environment = new TestEnvironment();
        T implInstance = testImpl.referenceImpl.apply(environment);

        if (testImpl.testCases == null) {
            DoubleValue node = new DoubleValue("aa");
            environment.map.add(node);

            testImpl.referenceNode(implInstance, node);
            Assertions.assertTrue(implInstance.containsReference(node));

            environment.map.remove(node);
            Assertions.assertFalse(implInstance.containsReference(node));
        } else {
            for (Map.Entry<Enum, BaseStructure> referenceTestCase : testImpl.testCases.entrySet()) {
                BaseStructure node = referenceTestCase.getValue();
                environment.map.add(node);

                testImpl.referenceNodeTestCase(environment, implInstance, referenceTestCase);
                Assertions.assertTrue(implInstance.containsReference(node));

                environment.map.remove(node);
                Assertions.assertFalse(implInstance.containsReference(node));
            }
        }
    }

    @ParameterizedTest()
    @MethodSource("getReferenceImplementations")
    public <T extends BaseStructureReferences> void reapplyCachedReferencesTest(ReferencesImplTest<T> testImpl) {
        TestEnvironment environment = new TestEnvironment();
        T implInstance = testImpl.referenceImpl.apply(environment);

        if (testImpl.testCases == null) {
            DoubleValue node = new DoubleValue("aa");
            testReferenceReapplyCached(environment, implInstance, node,
                    () -> testImpl.referenceNode(implInstance, node));
        } else {
            for (Map.Entry<Enum, BaseStructure> referenceTestCase : testImpl.testCases.entrySet()) {
                BaseStructure node = referenceTestCase.getValue();
                testReferenceReapplyCached(environment, implInstance, node,
                        () -> testImpl.referenceNodeTestCase(environment, implInstance, referenceTestCase));
            }
        }
    }

    private static <T extends BaseStructureReferences> void testReferenceReapplyCached(TestEnvironment environment,
                                                                                       T implInstance,
                                                                                       BaseStructure node,
                                                                                       Runnable reference) {
        environment.map.add(node);
        reference.run();
        Assertions.assertTrue(implInstance.containsReference(node));

        environment.manager.startCachingClearedReferences();
        environment.map.remove(node);
        Assertions.assertTrue(implInstance.containsReference(node));
        environment.map.add(node);
        environment.manager.reapplyCachedClearedReferences();
        Assertions.assertTrue(implInstance.containsReference(node));
    }


    @ParameterizedTest()
    @MethodSource("getReferenceImplementations")
    public <T extends BaseStructureReferences> void finalizeCachedReferencesTest(ReferencesImplTest<T> testImpl) {
        TestEnvironment environment = new TestEnvironment();
        T implInstance = testImpl.referenceImpl.apply(environment);

        if (testImpl.testCases == null) {
            DoubleValue node = new DoubleValue("aa");
            testReferenceFinalizeCached(environment, implInstance, node,
                    () -> testImpl.referenceNode(implInstance, node));
        } else {
            for (Map.Entry<Enum, BaseStructure> referenceTestCase : testImpl.testCases.entrySet()) {
                BaseStructure node = referenceTestCase.getValue();
                testReferenceFinalizeCached(environment, implInstance, node,
                        () -> testImpl.referenceNodeTestCase(environment, implInstance, referenceTestCase));
            }
        }
    }

    private static <T extends BaseStructureReferences> void testReferenceFinalizeCached(TestEnvironment environment,
                                                                                       T implInstance,
                                                                                       BaseStructure node,
                                                                                       Runnable reference) {
        environment.map.add(node);
        reference.run();
        Assertions.assertTrue(implInstance.containsReference(node));

        environment.manager.startCachingClearedReferences();
        environment.map.remove(node);
        Assertions.assertTrue(implInstance.containsReference(node));
        environment.map.add(node);
        environment.manager.finalizeCachedClearedReferences();
        environment.manager.reapplyCachedClearedReferences();
        Assertions.assertFalse(implInstance.containsReference(node));
    }


    public class TestEnvironment {
        DummyBaseHierarchyRoot root;
        DummyGenericMap map;
        ReferenceManager manager;
        EventBusManager busManager;
        EventTypeRegistry registry;

        public TestEnvironment() {
            root = new DummyBaseHierarchyRoot("r");
            map = new DummyGenericMap("map");
            root._addChild("var", map);
            root.initChildren();

            registry = new EventTypeRegistry();
            EventTypeRegistry.registerDefaultEventTypes(registry);

            manager = new ReferenceManager(root);
            busManager = new EventBusManager(manager, root, registry);
            manager.initEventSubscription(busManager);
        }
    }

    public class DummyGenericMap extends BaseValueMap<BaseStructure> {
        public DummyGenericMap(String id) {
            super(id);
        }

        @Override
        protected @NotNull BaseStructure instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
            return null;
        }
    }
}
