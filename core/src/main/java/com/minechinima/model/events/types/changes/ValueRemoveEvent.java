package com.minechinima.model.events.types.changes;

import com.minechinima.model.events.types.ChangeEvent;
import com.minechinima.model.logging.datainterfaces.IValueMap;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.operations.MapValueRemove;

public class ValueRemoveEvent extends ChangeEvent<MapValueRemove> {
    private final BaseStructure removedElement;

    public <V extends BaseStructure, T extends BaseStructure & IValueMap<V>>
    ValueRemoveEvent(T trigger, V removedElement) {
        super(trigger, new MapValueRemove(trigger.valueToData(removedElement)));
        this.removedElement = removedElement;
    }

    public BaseStructure getRemovedElement() {
        return this.removedElement;
    }
}
