package com.minechinima.operator.history.strategies;

import com.minechinima.model.logging.operations.ValueChange;
import com.minechinima.operator.history.MergeStrategy;
import org.jetbrains.annotations.Nullable;

public class ValueChangeMerge implements MergeStrategy<ValueChange, ValueChange> {
    @Override
    public @Nullable ValueChange merge(ValueChange current, ValueChange next) {
        if (!current.getPath().equals(next.getPath())) return null;
        return new ValueChange(current, next);
    }
}
