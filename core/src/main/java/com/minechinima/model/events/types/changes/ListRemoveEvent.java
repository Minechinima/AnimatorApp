package com.minechinima.model.events.types.changes;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.values.base.BaseValueOrderedCollection;
import com.minechinima.model.logging.operations.ListValueRemove;
import com.minechinima.model.logging.datainterfaces.ListElement;

public class ListRemoveEvent extends ChangeEvent<ListValueRemove> {
    private final ListElement removedValue;
    private final int index;

    public <J extends BaseStructure> ListRemoveEvent(BaseValueOrderedCollection<J> trigger, int index, ListElement<J> element) {
        super(trigger, new ListValueRemove(trigger.listElementToData(element), index));
        this.removedValue = element;
        this.index = index;
    }
}
