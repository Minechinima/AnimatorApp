package com.minechinima.model.values.vectors;

import com.minechinima.model.values.FloatValue;
import com.minechinima.model.values.base.ValueGroup;
import org.joml.Matrix3f;

public class Matrix3fValue extends ValueGroup {
    public final FloatValue m00 = new FloatValue("m00", 1F);
    public final FloatValue m01 = new FloatValue("m01", 0F);
    public final FloatValue m02 = new FloatValue("m02", 0F);
    public final FloatValue m10 = new FloatValue("m10", 0F);
    public final FloatValue m11 = new FloatValue("m11", 1F);
    public final FloatValue m12 = new FloatValue("m12", 0F);
    public final FloatValue m20 = new FloatValue("m20", 0F);
    public final FloatValue m21 = new FloatValue("m21", 0F);
    public final FloatValue m22 = new FloatValue("m22", 1F);

    public Matrix3fValue(String id) {
        super(id);
        this.addChild("m00", this.m00);
        this.addChild("m01", this.m01);
        this.addChild("m02", this.m02);
        this.addChild("m10", this.m10);
        this.addChild("m11", this.m11);
        this.addChild("m12", this.m12);
        this.addChild("m20", this.m20);
        this.addChild("m21", this.m21);
        this.addChild("m22", this.m22);
        this.initChildren();
    }

    public Matrix3f asMatrix3f() {
        return new Matrix3f(
                this.m00.get(), this.m01.get(), this.m02.get(),
                this.m10.get(), this.m11.get(), this.m12.get(),
                this.m20.get(), this.m21.get(), this.m22.get()
        );
    }

    public void set(Matrix3f m) {
        this.m00.set(m.m00);
        this.m01.set(m.m01);
        this.m02.set(m.m02);
        this.m10.set(m.m10);
        this.m11.set(m.m11);
        this.m12.set(m.m12);
        this.m20.set(m.m20);
        this.m21.set(m.m21);
        this.m22.set(m.m22);
    }
}
