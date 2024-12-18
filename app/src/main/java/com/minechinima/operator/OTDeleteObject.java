package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.ui.BBOperatorContext;

public class OTDeleteObject extends OperatorType<BBOperatorContext, OperatorProperties> {
    public OTDeleteObject(String title, String description) {
        super(title, description, true, false, false);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.context.activeObject.getReference() != null;
    }

    @Override
    public OperatorProperties createProps() {
        return null;
    }
}
