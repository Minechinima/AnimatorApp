package com.minechinima.model.events.types.changes;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.events.types.ChangeEvent;
import com.minechinima.model.logging.operations.ValueChange;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.base.BaseStructure;

public class ValueChangeEvent extends ChangeEvent<ValueChange> {
    private final BaseType<?> valueBeforeSerialized;
    private final Object valueBefore;
    private final BaseType<?> valueAfterSerialized;
    private final Object valueAfter;

    /**
     * @param trigger the trigger (before the value is changed)
     * @param valueAfter the value that is set. No valueBefore parameter forces to give the old BaseValue state.
     */
    public <T extends BaseStructure & IValue<J>, J> ValueChangeEvent(T trigger, J valueBefore, J valueAfter) {
        super(trigger, new ValueChange(trigger, trigger.valueToData(valueBefore)));
        this.valueBeforeSerialized = trigger.valueToData(valueBefore);
        this.valueAfterSerialized = trigger.valueToData(trigger.get());
        this.valueAfter = valueAfter;
        this.valueBefore = valueBefore;
    }

    //TODO rethink this, this could lead to valueBefore and serialized not matching, not good api, maybe add new event for valuepointer
    public <T extends BaseStructure & IValue<J>, J> ValueChangeEvent(T trigger, BaseType<?> valueBeforeSerialized, J valueBefore,
                                                                     BaseType<?> valueAfterSerialized, J valueAfter) {
        super(trigger, new ValueChange(trigger, valueBeforeSerialized));
        this.valueBeforeSerialized = valueBeforeSerialized;
        this.valueAfterSerialized = valueAfterSerialized;
        this.valueAfter = valueAfter;
        this.valueBefore = valueBefore;
    }

    public BaseType<?> getValueBefore() {
        return this.valueBeforeSerialized;
    }

    public BaseType<?> getValueAfter() {
        return this.valueAfterSerialized;
    }
}
