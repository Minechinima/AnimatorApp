package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.properties.ScaleProps;
import org.jetbrains.annotations.NotNull;

public class OTScaleObject extends OTTransformObject<ScaleObject, ScaleProps> {
    public OTScaleObject(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.get() != null;
    }

    @NotNull
    @Override
    public ScaleObject getOperator() {
        return new ScaleObject(new ScaleProps());
    }

    @Override
    public boolean confirmOnReleaseDefault() {
        return false;
    }

    @Override
    public ScaleObject getOperator(ScaleProps props) {
        return new ScaleObject(props);
    }

    @Override
    public ScaleProps createProps() {
        return new ScaleProps();
    }
}
