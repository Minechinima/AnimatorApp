package com.minechinima.operator;

import com.minechinima.model.BBContext;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;

public class OTTranslateObject extends OperatorTypePropertyProvider<TranslateProps, BBOperatorContext> {
    public OTTranslateObject(String id, String title, String description) {
        super(id, title, description, UNDO | ADJUSTLAST | REPEATLAST);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.bbContext().activeObject.getReference() != null;
    }

    @Override
    public TranslateProps createProps() {
        return new TranslateProps("");
    }
}
