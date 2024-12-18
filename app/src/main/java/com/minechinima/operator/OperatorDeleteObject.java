package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.flags.OpExecuteFlag;
import com.minechinima.ui.BBOperatorContext;

public class OperatorDeleteObject extends Operator<OperatorProperties, BBOperatorContext>{
    public OperatorDeleteObject(OperatorProperties properties) {
        super(properties);
    }

    public OpExecuteFlag execute(BBOperatorContext context) throws UserException {
        System.out.println("DELETING");
        context.context.database.objects.remove(context.context.activeObject.getReference());
        return OpExecuteFlag.FINISHED;
    }
}
