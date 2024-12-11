package com.minechinima.model;

import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.model.base.ValuePointer;

public class BBContext extends ValueGroup {
    public final ValuePointer<BaseObject> activeObject;

    public final BBDatabase database;

    public BBContext(String id, BBDatabase database) {
        super(id);
        this.database = database;
        this.activeObject = new ValuePointer<>("active_object", BaseObject.class, this.database);
        this.addChild("active_object", this.activeObject);
    }
}
