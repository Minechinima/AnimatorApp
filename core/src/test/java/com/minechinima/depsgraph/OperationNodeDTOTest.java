package com.minechinima.depsgraph;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.dummies.DummyBaseImmutableHierarchy;
import com.minechinima.model.base.dummies.DummyBaseStructure;
import com.minechinima.model.utils.ValuePath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests for the {@link OperationNodeDTO} system and the
 * {@link OperationNode.EvalFunction}/{@link OperationNode.DTOFactory} registration mechanism.
 */
public class OperationNodeDTOTest {

    private DummyBaseImmutableHierarchy originalData;
    private DummyBaseImmutableHierarchy copyData;
    private DummyBaseStructure originalAttribute;
    private DummyBaseStructure copyAttribute;

    private DataNode<DummyBaseImmutableHierarchy> dataNode;
    private ComponentNode<DummyBaseImmutableHierarchy> component;
    private DepsGraph depsGraph;

    @BeforeEach
    public void setup() {
        /* Set up original data with a child attribute */
        originalData = new DummyBaseImmutableHierarchy("root", ValuePath.Format.PATH);
        originalAttribute = new DummyBaseStructure("attr1");
        originalData._add("attr1", originalAttribute);
        originalData.initChildren();

        /* Set up a separate copy (simulating the evaluated copy) */
        copyData = new DummyBaseImmutableHierarchy("root", ValuePath.Format.PATH);
        copyAttribute = new DummyBaseStructure("attr1");
        copyData._add("attr1", copyAttribute);
        copyData.initChildren();

        dataNode = new DataNode<>(originalData);
        depsGraph = new DepsGraph();

        /* Build a component with entry/exit operation nodes.
         * We only need the component for creating operation nodes in tests,
         * so we set up its own entry/exit without attaching to the DataNode's entry/exit. */
        component = new ComponentNode<>(dataNode, ComponentTypes.DATA_ENTRY);
        OperationNode<DummyBaseImmutableHierarchy> entry = new OperationNode<>(component, OperationCodes.GENERIC_ENTRY);
        OperationNode<DummyBaseImmutableHierarchy> exit = new OperationNode<>(component, OperationCodes.GENERIC_EXIT);
        component.setEntry(entry);
        component.setExit(exit);
    }

    /**
     * Default behavior: when no custom DTO is registered, the eval function
     * receives an {@link OperationNodeDTO} with the data node copy.
     */
    @Test
    public void testDefaultDTOReceivesCopy() {
        AtomicReference<DummyBaseImmutableHierarchy> received = new AtomicReference<>();

        OperationNode<DummyBaseImmutableHierarchy> opNode = component.addOperation(
                (graph, dto) -> received.set(dto.getCopy()),
                OperationCodes.GENERIC_ENTRY,
                "defaultTest"
        );

        opNode.evaluate(depsGraph, copyData);

        Assertions.assertSame(copyData, received.get(),
                "Default DTO should provide the evaluated copy.");
    }

    /**
     * The DTO should never expose the original data.
     * The eval function only receives the copy through the DTO.
     */
    @Test
    public void testNoAccessToOriginal() {
        AtomicReference<DummyBaseImmutableHierarchy> received = new AtomicReference<>();

        OperationNode<DummyBaseImmutableHierarchy> opNode = component.addOperation(
                (graph, dto) -> received.set(dto.getCopy()),
                OperationCodes.GENERIC_ENTRY,
                "noOriginalTest"
        );

        opNode.evaluate(depsGraph, copyData);

        Assertions.assertNotSame(originalData, received.get(),
                "The eval function must not receive the original data.");
        Assertions.assertSame(copyData, received.get(),
                "The eval function should receive the copy data.");
    }

    /**
     * NOOP nodes (null func) should not execute any logic.
     */
    @Test
    public void testNOOPNodeDoesNotExecute() {
        OperationNode<DummyBaseImmutableHierarchy> opNode = new OperationNode<>(
                component, OperationCodes.GENERIC_ENTRY, "noopTest"
        );

        Assertions.assertTrue(opNode.isNOOP());

        /* should not throw */
        opNode.evaluate(depsGraph, copyData);
    }

    /**
     * Custom DTO with specific subdata: the eval function receives a typed
     * DTO subclass that provides direct access to specific child data.
     * Type safety is ensured at compile time through generics.
     */
    @Test
    public void testCustomDTOWithSubdata() {
        AtomicReference<DummyBaseStructure> receivedAttribute = new AtomicReference<>();
        AtomicReference<DummyBaseImmutableHierarchy> receivedCopy = new AtomicReference<>();

        /*
         * The DTOFactory creates a typed DTO from the copy,
         * extracting specific subdata.
         */
        OperationNode<DummyBaseImmutableHierarchy> opNode = component.addOperation(
                AttributeDTO::new,
                (graph, dto) -> {
                    /* dto is inferred as AttributeDTO - type safe access */
                    receivedAttribute.set(dto.getAttribute());
                    receivedCopy.set(dto.getCopy());
                },
                OperationCodes.GENERIC_ENTRY,
                "customDTOTest"
        );

        opNode.evaluate(depsGraph, copyData);

        Assertions.assertSame(copyData, receivedCopy.get(),
                "Custom DTO should still provide the root copy.");
        Assertions.assertSame(copyAttribute, receivedAttribute.get(),
                "Custom DTO should provide the specific subdata from the copy, not the original.");
        Assertions.assertNotSame(originalAttribute, receivedAttribute.get(),
                "Custom DTO must not expose original database objects.");
    }

    /**
     * Verify that the DTO factory receives the copy, not the original.
     */
    @Test
    public void testDTOFactoryReceivesCopy() {
        AtomicReference<DummyBaseImmutableHierarchy> factoryInput = new AtomicReference<>();

        OperationNode<DummyBaseImmutableHierarchy> opNode = component.addOperation(
                (copy) -> {
                    factoryInput.set(copy);
                    return new OperationNodeDTO<>(copy);
                },
                (graph, dto) -> { /* no-op for this test */ },
                OperationCodes.GENERIC_ENTRY,
                "factoryInputTest"
        );

        opNode.evaluate(depsGraph, copyData);

        Assertions.assertSame(copyData, factoryInput.get(),
                "DTOFactory should receive the evaluated copy, not the original.");
        Assertions.assertNotSame(originalData, factoryInput.get(),
                "DTOFactory must not receive the original database object.");
    }

    /**
     * Verify that evaluate does nothing on NOOP and works on non-NOOP.
     */
    @Test
    public void testEvaluateCallsEvalFunction() {
        AtomicBoolean called = new AtomicBoolean(false);

        OperationNode<DummyBaseImmutableHierarchy> opNode = component.addOperation(
                (graph, dto) -> called.set(true),
                OperationCodes.GENERIC_ENTRY,
                "evalCallTest"
        );

        Assertions.assertFalse(called.get());
        opNode.evaluate(depsGraph, copyData);
        Assertions.assertTrue(called.get(), "evaluate should invoke the eval function.");
    }

    /**
     * Custom DTO with scope: verify operation nodes with custom DTOs
     * can also have a DataScope.
     */
    @Test
    public void testCustomDTOWithScope() {
        DataScope scope = new DataScope(java.util.Set.of(copyAttribute), java.util.Set.of());
        AtomicBoolean called = new AtomicBoolean(false);

        OperationNode<DummyBaseImmutableHierarchy> opNode = component.addOperation(
                AttributeDTO::new,
                (graph, dto) -> {
                    called.set(true);
                    Assertions.assertNotNull(dto.getAttribute());
                },
                OperationCodes.GENERIC_ENTRY,
                "scopeTest",
                scope
        );

        Assertions.assertNotNull(opNode.getScope());
        opNode.evaluate(depsGraph, copyData);
        Assertions.assertTrue(called.get());
    }

    /**
     * Custom DTO subclass that extracts a specific child attribute from the copy.
     * Demonstrates type-safe subdata access.
     */
    private static class AttributeDTO extends OperationNodeDTO<DummyBaseImmutableHierarchy> {
        private final DummyBaseStructure attribute;

        public AttributeDTO(DummyBaseImmutableHierarchy copy) {
            super(copy);
            /* Extract specific subdata from the evaluated copy */
            this.attribute = (DummyBaseStructure) copy.getChildByValuePath("attr1");
        }

        public DummyBaseStructure getAttribute() {
            return attribute;
        }
    }
}
