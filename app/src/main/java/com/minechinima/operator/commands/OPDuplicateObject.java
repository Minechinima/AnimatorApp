package com.minechinima.operator.commands;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.BBOperatorContext;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.operator.properties.DuplicateProps;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class OPDuplicateObject extends IPropertyOperator<BBOperatorContext, DuplicateProps> {
    public OPDuplicateObject(DuplicateProps props) {
        super(props);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> execute(BBOperatorContext context) throws UserException {
        IControllerCommand duplicate = context.bbContext().database.objects.controller().duplicateActiveObject(context.bbContext());
        return new OPReturnValue<>(OpExecuteFlag.FINISHED, new LinkedList<>(List.of(duplicate)));
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        OPReturnValue<OpExecuteFlag> retVal = this.execute(context);
        return new OPReturnValue<>(OpInvokeFlag.from(retVal.flag()), retVal.commands());
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return null;
    }
}
