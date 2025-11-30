package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;
import org.jetbrains.annotations.NotNull;

public class OTTranslateObject extends OperatorTypePropertyProvider<TranslateProps, TranslateObject, BBOperatorContext> {
    public OTTranslateObject(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.get() != null;
    }

    @Override
    public @NotNull TranslateObject getOperator() {
        return null;
    }

    @Override
    public TranslateObject getOperator(TranslateProps props) {
        return null;
    }

    @Override
    public TranslateProps createProps() {
        return new TranslateProps();
    }
}
