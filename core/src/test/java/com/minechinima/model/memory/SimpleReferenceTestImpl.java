package com.minechinima.model.memory;

import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class SimpleReferenceTestImpl extends ReferencesImplTest<SimpleReference> {
    public SimpleReferenceTestImpl() {
        super(testEnvironment -> new SimpleReference<>(testEnvironment.manager));
    }

    @Override
    public void referenceNode(SimpleReference instance, BaseStructure node) {
        instance.set(node);
    }
}
