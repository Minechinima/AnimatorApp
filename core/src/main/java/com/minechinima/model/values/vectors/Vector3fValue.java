package com.minechinima.model.values.vectors;

import com.minechinima.controller.factories.IControllerFactoryProvider;
import com.minechinima.controller.factories.VectorControllerFactory;
import com.minechinima.model.api.values.IDataValue;
import com.minechinima.model.api.values.vectors.IVector3f;
import com.minechinima.model.values.FloatValue;
import com.minechinima.model.values.base.ValueGroup;
import org.joml.Vector3f;

import java.util.*;

public class Vector3fValue extends ValueGroup implements IControllerFactoryProvider<VectorControllerFactory>, IVector3f {
    public final FloatValue x;
    public final FloatValue y;
    public final FloatValue z;
    private final VectorControllerFactory controllers = new VectorControllerFactory(this);

    public Vector3fValue(String id) {
        this(id, null, 0, 0, 0);
    }

    public Vector3fValue(String id, float x, float y, float z) {
        this(id, null, x, y, z);
    }

    protected Vector3fValue(String id, UUID uuid, float x, float y, float z) {
        super(id, uuid);
        this.addChild("x", this.x = new FloatValue("x", x));
        this.addChild("y", this.y = new FloatValue("y", y));
        this.addChild("z", this.z = new FloatValue("z", z));
        this.initChildren();
    }

    public Vector3f asVector3f() {
        return new Vector3f(this.x.get(), this.y.get(), this.z.get());
    }

    @Override
    public VectorControllerFactory controller() {
        return this.controllers;
    }

    @Override
    public IDataValue<Float> x() {
        return this.x;
    }

    @Override
    public IDataValue<Float> y() {
        return this.y;
    }

    @Override
    public IDataValue<Float> z() {
        return this.z;
    }
}
