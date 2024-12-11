package com.minechinima.model.events.types.changes;

import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.operations.MapValueRemove;

public class ValueRemoveEvent extends ChangeEvent<MapValueRemove> {
    private final BaseStructure removedElement;

    public <J extends BaseStructure> ValueRemoveEvent(BaseValueMap<J> trigger, J removedElement) {
        super(trigger, new MapValueRemove(trigger.valueToData(removedElement)));
        this.removedElement = removedElement;
    }

    public BaseStructure getRemovedElement() {
        return this.removedElement;
    }
}
