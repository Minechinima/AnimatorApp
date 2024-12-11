package com.minechinima.model.events.types.changes;

import com.minechinima.model.logging.datainterfaces.IValueList;
import com.minechinima.model.logging.datainterfaces.IValueMap;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.operations.MapValueAdd;

public class ValueAddEvent extends ChangeEvent<MapValueAdd> {
    private final BaseStructure addedElement;

    public <V extends BaseStructure, T extends BaseStructure & IValueMap<V>>
    ValueAddEvent(T trigger, V addedElement) {
        super(trigger, new MapValueAdd(addedElement));
        this.addedElement = addedElement;
    }

    public BaseStructure getAddedElement() {
        return this.addedElement;
    }
}
