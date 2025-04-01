package com.minechinima.operator;

import com.minechinima.model.BBContext;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.OperatorType;

public class OTDeleteObject extends OperatorType<BBOperatorContext> {
    public OTDeleteObject(String id, String title, String description) {
        super(id, title, description, UNDO);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.getReference() != null;
    }
}
