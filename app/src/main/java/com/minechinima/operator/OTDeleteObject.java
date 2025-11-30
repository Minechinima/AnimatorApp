package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.OperatorType;
import org.jetbrains.annotations.NotNull;

public class OTDeleteObject extends OperatorType<BBOperatorContext, DeleteObject> {
    public OTDeleteObject(String id, String title, String description) {
        super(id, title, description, true);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.get() != null;
    }

    @NotNull
    @Override
    public DeleteObject getOperator() {
        return new DeleteObject();
    }
}
