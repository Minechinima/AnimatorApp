package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.OperatorType;

public class OTRedo extends OperatorType {
    public OTRedo(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean poll(IContext context) throws UserException {
        return context.getUIContext().manager().getOperatorManager().canRedo();
    }
}
