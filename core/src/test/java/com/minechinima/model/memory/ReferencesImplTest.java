package com.minechinima.model.memory;

import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ReferencesImplTest<T extends BaseStructureReferences> {
    public final Function<ReferenceManagerTest.TestEnvironment, T> referenceImpl;
    /**
     * If the reference only accepts special basestructures or
     * has different reference methods, this can be used to define
     * different references for different reference types.
     * <br><br>
     * null if it can accept any basestructure and only has one type of reference method
     */
    @Nullable
    public final Map<Enum, BaseStructure> testCases;

    public ReferencesImplTest(Function<ReferenceManagerTest.TestEnvironment, T> referenceImpl,
                              @Nullable Map<Enum, BaseStructure> testCases) {
        this.referenceImpl = referenceImpl;
        this.testCases = testCases;
    }

    public ReferencesImplTest(Function<ReferenceManagerTest.TestEnvironment, T> referenceImpl) {
        this(referenceImpl, null);
    }


    public void referenceNode(T implInstance, BaseStructure node) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void referenceNodeTestCase(ReferenceManagerTest.TestEnvironment environment, T implInstance, Map.Entry<Enum, BaseStructure> testCase) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
