package com.minechinima.operator;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.ui.BBOperatorContext;
import com.minechinima.utils.Pair;

import java.util.Queue;

public record TranslateObject(TranslateProps props) implements IPropertyOperator<BBOperatorContext, TranslateProps>, IRepeatableOperator<BBOperatorContext> {

    @Override
    public Pair<OpInvokeFlag, Queue<Object>> invoke(BBOperatorContext context) throws UserException {
        return new Pair<>(OpInvokeFlag.CANCELLED, null);
    }

    @Override
    public Pair<OpExecuteFlag, Queue<Object>> executeOnPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return this.execute(context);
    }

    @Override
    public void cancelUIMode(BBOperatorContext context) {

    }

    @Override
    public Pair<OpExecuteFlag, Queue<Object>> repeat(BBOperatorContext context) throws UserException {
        return this.execute(context);
    }
}
