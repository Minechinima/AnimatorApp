package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;

import java.util.LinkedList;
import java.util.List;

public class DeleteObject implements IOperator<BBOperatorContext> {
    public OPReturnValue<OpExecuteFlag> execute(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpExecuteFlag.CANCELLED, null);
        BaseObject selection = context.bbContext().activeObject.get();
        IControllerCommand delete = context.bbContext().database.objects.controller().deleteObject(selection);
        return new OPReturnValue<>(OpExecuteFlag.FINISHED, delete);
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        OPReturnValue<OpExecuteFlag> retval = this.execute(context);
        return new OPReturnValue<>(OpInvokeFlag.from(retval.flag()), retval.commands());
    }
}
