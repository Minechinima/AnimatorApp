package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.AbstractOperatorType;
import com.minechinima.operator.ot.AppOperatorType;
import com.minechinima.ui.BBOperatorContext;

public class OTTranslateObject extends AppOperatorType<BBOperatorContext> implements AbstractOperatorType.PropertyProvider<TranslateProps> {
    public OTTranslateObject(String title, String description) {
        super(title, description, UNDO | ADJUSTLAST | REPEATLAST);
    }

    @Override
    public boolean poll(BBOperatorContext context) throws UserException {
        return context.context.activeObject.getReference() != null;
    }

    @Override
    public TranslateProps createProps() {
        return new TranslateProps("");
    }
}
