package com.minechinima.model.api.values.vectors;

import com.minechinima.model.api.IDataTree;
import com.minechinima.model.api.values.IDataValue;
import org.joml.Vector3f;

public interface IVector3f extends IDataTree {
    IDataValue<Float> x();
    IDataValue<Float> y();
    IDataValue<Float> z();

    default Vector3f asVector3f() {
        return new Vector3f(this.x().get(), this.y().get(), this.z().get());
    }
}
