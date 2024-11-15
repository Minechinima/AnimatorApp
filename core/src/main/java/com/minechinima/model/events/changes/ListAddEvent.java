package com.minechinima.model.events.changes;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueOrderedCollection;
import com.minechinima.model.dataoperations.ListValueInsert;

import java.util.UUID;

public class ListAddEvent extends ChangeEvent<ListValueInsert> {
    private final UUID listElementUUID;
    private final BaseStructure addedElement;

    public <J extends BaseStructure> ListAddEvent(BaseValueOrderedCollection<J> trigger, J addedElement) {
        super(trigger, new ListValueInsert(trigger.listUUIDOf(addedElement)));
        this.listElementUUID = trigger.listUUIDOf(addedElement);
        this.addedElement = addedElement;
    }
}
