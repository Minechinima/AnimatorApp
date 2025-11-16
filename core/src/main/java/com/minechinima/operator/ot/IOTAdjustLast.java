package com.minechinima.operator.ot;

import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.commands.IAdjustableOperator;

public interface IOTAdjustLast<O extends IAdjustableOperator<?, ?>> {
    O getOperator();
}
