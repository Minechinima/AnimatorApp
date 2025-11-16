package com.minechinima.operator.ot;

import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.commands.IAdjustableOperator;
import com.minechinima.operator.ot.commands.IRepeatableOperator;

public interface IOTRepeatable<O extends IRepeatableOperator<?>> {
    O getOperator();
}
