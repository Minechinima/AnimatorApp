package com.minechinima.operator;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;

public class RotateObject extends IAdjustableOperator<BBOperatorContext, TranslateProps> {
    public RotateObject(TranslateProps props) {
        super(props);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> adjust(BBOperatorContext context, BaseStructure prop) throws UserException {
        return null;
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return null;
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        return null;
    }
}
