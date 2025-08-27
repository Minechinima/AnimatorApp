package com.minechinima.operator.history.strategies;

import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.logging.operations.Type;
import com.minechinima.model.logging.operations.ValueChange;
import com.minechinima.operator.history.ParallelCheckStrategy;

public class ValueChangeParallelCheck implements ParallelCheckStrategy<ValueChange, IDataOperation> {
    @Override
    public boolean canBeParallel(ValueChange a, IDataOperation b) {
        return b.getOperationType() == Type.UPDATE;
    }
}
