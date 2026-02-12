package com.minechinima.model.events.types.changes;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.events.types.ChangeEvent;
import com.minechinima.model.logging.datainterfaces.IValueList;
import com.minechinima.model.logging.operations.ListValueRemove;
import com.minechinima.model.logging.datainterfaces.ListElement;

public class ListRemoveEvent extends ChangeEvent<ListValueRemove> {
    private final ListElement removedValue;
    private final int index;

    public <V extends BaseStructure, T extends BaseStructure & IValueList<V>>
    ListRemoveEvent(T trigger, int index, ListElement<V> element) {
        super(trigger, new ListValueRemove(trigger.listElementToData(element), index));
        this.removedValue = element;
        this.index = index;
    }
}
