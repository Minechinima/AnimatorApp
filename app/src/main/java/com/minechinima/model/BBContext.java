package com.minechinima.model;

import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.operator.IContext;
import com.minechinima.operator.UIOperatorContext;

public class BBContext extends ValueGroup {
    public final ValuePointer<BaseObject> activeObject;

    /* not part of BBContext tree */
    public final BBDatabase database;

    public BBContext(String id, BBDatabase database) {
        super(id);
        this.database = database;
        this.addChild("active_object", this.activeObject = new ValuePointer<>("active_object", BaseObject.class, this.database));
    }
}
