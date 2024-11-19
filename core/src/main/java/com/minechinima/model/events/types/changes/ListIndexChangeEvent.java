package com.minechinima.model.events.types.changes;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.dataoperations.datainterfaces.ListElement;
import com.minechinima.model.dataoperations.ListChangeIndex;

public class ListIndexChangeEvent extends ChangeEvent<ListChangeIndex> {
    private final int oldIndex;
    private final int newIndex;
    private final ListElement movedValue;

    public ListIndexChangeEvent(BaseStructure trigger, int oldIndex, int newIndex, ListElement movedValue) {
        super(trigger, new ListChangeIndex(oldIndex, movedValue.uuid()));
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
        this.movedValue = movedValue;
    }
}
