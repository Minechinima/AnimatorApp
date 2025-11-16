package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.OperatorType;
import org.jetbrains.annotations.NotNull;

public class OTRedo extends OperatorType<IContext, RedoOperator> {
    public OTRedo(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean poll(IContext context) throws UserException {
        return context.getUIContext().manager().getOperatorManager().canRedo();
    }

    @Override
    public @NotNull RedoOperator getOperator() {
        return new RedoOperator();
    }
}
