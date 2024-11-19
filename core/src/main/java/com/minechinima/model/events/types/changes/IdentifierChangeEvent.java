package com.minechinima.model.events.types.changes;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.dataoperations.IdentifierChange;

public class IdentifierChangeEvent extends ChangeEvent<IdentifierChange> {
    private final String idAfter;
    private final String idBefore;

    public IdentifierChangeEvent(BaseStructure trigger, String idBefore, String idAfter) {
        super(trigger, new IdentifierChange(idBefore));
        this.idAfter = idAfter;
        this.idBefore = idBefore;
    }
}
