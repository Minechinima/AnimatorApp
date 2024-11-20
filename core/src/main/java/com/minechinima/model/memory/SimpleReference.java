package com.minechinima.model.memory;

import com.minechinima.model.base.BaseStructure;

public class SimpleReference<T extends BaseStructure> extends BaseStructureReferences {
    private T value;
    private T cache;

    public SimpleReference(ReferenceManager manager) {
        super(manager);
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        T prevValue = this.value;
        this.value = value;
        if (prevValue != null && value != prevValue) {
            this.afterRemoveReference(prevValue);
        }

        if (value != null) {
            this.afterAddReference(value);
        }
    }

    @Override
    public void prepareClearReferences(BaseStructure reference) {
        this.cache = this.value;
        this.value = null;
    }

    @Override
    public void finalizeClearReferences() {
        this.cache = null;
        this.value = null;
    }

    @Override
    public void undoClearReferences() {
        this.value = this.cache;
        this.cache = null;
    }

    @Override
    public boolean containsReference(BaseStructure reference) {
        return reference != null && (this.value == reference || this.cache == reference);
    }
}
