package com.minechinima.model.events.types.changes;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.datainterfaces.IDMutable;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.logging.operations.IdentifierChange;

public class IdentifierChangeEvent extends ChangeEvent<IdentifierChange> {
    private final String idAfter;
    private final String idBefore;

    public IdentifierChangeEvent(BaseStructure trigger, String idBefore, String idAfter) {
        super(trigger, new IdentifierChange(trigger, idBefore));
        this.idAfter = idAfter;
        this.idBefore = idBefore;
    }
}
