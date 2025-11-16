package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.utils.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DeleteObject implements IOperator<BBOperatorContext> {
    public Pair<OpExecuteFlag, Queue<IControllerCommand>> execute(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.getReference() == null) return new Pair<>(OpExecuteFlag.CANCELLED, null);
        BaseObject selection = context.bbContext().activeObject.getReference();
        IControllerCommand delete = context.bbContext().database.objects.controllers().deleteObject(selection);
        return new Pair<>(OpExecuteFlag.FINISHED, new LinkedList<>(List.of(delete)));
    }

    @Override
    public Pair<OpInvokeFlag, Queue<IControllerCommand>> invoke(BBOperatorContext context) throws UserException {
        Pair<OpExecuteFlag, Queue<IControllerCommand>> retval = this.execute(context);
        return new Pair<>(OpInvokeFlag.from(retval.getA()), retval.getB());
    }
}
