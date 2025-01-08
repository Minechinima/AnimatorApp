package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.AppOperatorType;
import com.minechinima.ui.BBOperatorContext;

public class OTDeleteObject extends AppOperatorType<BBOperatorContext> {
    public OTDeleteObject(String title, String description) {
        super(title, description, UNDO);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.context.activeObject.getReference() != null;
    }
}
