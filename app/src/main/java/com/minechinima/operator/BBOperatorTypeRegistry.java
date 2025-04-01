package com.minechinima.operator;

import com.minechinima.model.BBContext;

public class BBOperatorTypeRegistry extends OperatorTypeRegistry<BBOperatorContext>  {
    public final OTTranslateObject move = new OTTranslateObject("translate", "translate", "Move given object");
    public final OTDeleteObject delete = new OTDeleteObject("delete", "delete object", "Delete");

    public BBOperatorTypeRegistry(String id) {
        super(id);
        this.addChild("translate", this.move);
        this.addChild("delete", this.delete);

        this.delete.initChildren();
        this.move.initChildren();
    }
}
