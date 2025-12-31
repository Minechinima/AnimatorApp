package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.commands.OPDuplicateObject;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;
import com.minechinima.operator.properties.DuplicateProps;
import org.jetbrains.annotations.NotNull;

public class OTDuplicateObject extends OperatorTypePropertyProvider<DuplicateProps, OPDuplicateObject, BBOperatorContext> {
    public OTDuplicateObject(String id, String title, String description) {
        super(id, title, description, true);
    }

    @Override
    public DuplicateProps createProps() {
        return new DuplicateProps();
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return _poll(context);
    }

    public static boolean _poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.get() != null;
    }

    @NotNull
    @Override
    public OPDuplicateObject getOperator() {
        return new OPDuplicateObject(new DuplicateProps());
    }

    @Override
    public OPDuplicateObject getOperator(DuplicateProps props) {
        return new OPDuplicateObject(props);
    }
}
