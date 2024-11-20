package com.minechinima.model.events.types.changes;

import com.minechinima.model.base.BaseValueMap;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.dataoperations.operations.MapValueAdd;

public class ValueAddEvent extends ChangeEvent<MapValueAdd> {
    private final BaseStructure addedElement;

    public <T extends BaseStructure> ValueAddEvent(BaseValueMap<T> trigger, T addedElement) {
        super(trigger, new MapValueAdd(addedElement));
        this.addedElement = addedElement;
    }

    public BaseStructure getAddedElement() {
        return this.addedElement;
    }
}
