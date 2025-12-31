package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;
import com.minechinima.operator.properties.TranslateProps;
import org.jetbrains.annotations.NotNull;

public class OTTranslateObject extends OperatorTypePropertyProvider<TranslateProps, TranslateObject, BBOperatorContext> implements IOTModal<TranslateObject> {
    public OTTranslateObject(String id, String title, String description) {
        super(id, title, description, true);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.get() != null;
    }

    @Override
    public @NotNull TranslateObject getOperator() {
        return new TranslateObject(new TranslateProps());
    }

    @Override
    public boolean confirmOnReleaseDefault() {
        return false;
    }

    @Override
    public TranslateObject getOperator(TranslateProps props) {
        return new TranslateObject(props);
    }

    @Override
    public TranslateProps createProps() {
        return new TranslateProps();
    }
}
