package com.minechinima.model.values.vectors;

import com.minechinima.model.values.FloatValue;
import com.minechinima.model.values.base.ValueGroup;
import org.joml.Vector3f;

import java.util.UUID;

public class Vector3fValue extends ValueGroup {
    public final FloatValue x = new FloatValue("x");
    public final FloatValue y = new FloatValue("y");
    public final FloatValue z = new FloatValue("z");

    public Vector3fValue(String id) {
        this(id, null);
    }

    protected Vector3fValue(String id, UUID uuid) {
        super(id, uuid);
        this.addChild("x", this.x);
        this.addChild("y", this.y);
        this.addChild("z", this.z);
    }

    public Vector3f asVector3f() {
        return new Vector3f(this.x.get(), this.y.get(), this.z.get());
    }
}
