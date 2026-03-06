package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;
import com.minechinima.operator.properties.TranslateProps;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class OTRotateObject extends OTTransformObject<RotateObject, RotateProps> {

    public OTRotateObject(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean useReleaseEvent() {
        return false;
    }

    @Override
    public RotateObject getOperator(RotateProps props) {
        return new RotateObject(props);
    }

    @Override
    public RotateProps createProps() {
        return new RotateProps();
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.get() != null;
    }

    @NotNull
    @Override
    public RotateObject getOperator() {
        return new RotateObject(new RotateProps());
    }
}
