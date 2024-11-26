package com.minechinima.model.events.types.changes;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.logging.operations.ValueChange;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.base.BaseStructure;

public class ValueChangeEvent extends ChangeEvent<ValueChange> {
    private final BaseType<?> valueBefore;
    private final BaseType<?> valueAfter;

    /**
     * @param trigger the trigger (before the value is changed)
     * @param valueAfter the value that is set. No valueBefore parameter forces to give the old BaseValue state.
     */
    public <T extends BaseStructure & IValue> ValueChangeEvent(T trigger, BaseType<?> valueBefore, BaseType<?> valueAfter) {
        super(trigger, new ValueChange(trigger, valueBefore));
        this.valueBefore = valueBefore;
        this.valueAfter = valueAfter;
    }

    public Object getValueBefore() {
        return this.valueBefore;
    }

    public Object getValueAfter() {
        return this.valueAfter;
    }
}
