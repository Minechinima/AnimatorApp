package com.minechinima.model.events.types.changes;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.datainterfaces.IValueList;
import com.minechinima.model.values.base.BaseValueOrderedCollection;
import com.minechinima.model.logging.operations.ListValueInsert;

import java.util.UUID;

public class ListAddEvent extends ChangeEvent<ListValueInsert> {
    private final UUID listElementUUID;
    private final BaseStructure addedElement;

    public <V extends BaseStructure, T extends BaseStructure & IValueList<V>>
    ListAddEvent(T trigger, V addedElement) {
        super(trigger, new ListValueInsert(trigger.listUUIDOf(addedElement)));
        this.listElementUUID = trigger.listUUIDOf(addedElement);
        this.addedElement = addedElement;
    }
}
