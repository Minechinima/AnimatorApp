package com.minechinima.model;

import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.utils.ValuePath;

public class BBNameSpace extends BaseHierarchyRoot {
    public final BBDatabase data = new BBDatabase("data");

    public BBNameSpace(String id) {
        super(id, ValuePath.Format.PATH);
        this.addChild("data", this.data);
        this.data.initChildren();
    }
}
