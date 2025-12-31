package com.minechinima.operator;

import com.minechinima.operator.ot.MacroOperatorType;

public class OTDuplicateMoveObject extends MacroOperatorType<BBOperatorContext> {
    public OTDuplicateMoveObject(String id, String title, String description,
                                 OTDuplicateObject otDuplicateObject, OTTranslateObject translateObject) {
        super(id, title, description, true,
                new Macro<>(otDuplicateObject),
                new Macro<>(translateObject));
    }
}
