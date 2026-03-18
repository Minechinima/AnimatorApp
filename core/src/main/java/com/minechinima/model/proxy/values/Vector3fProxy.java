package com.minechinima.model.proxy.values;

import com.minechinima.model.api.values.IDataValue;
import com.minechinima.model.api.values.vectors.IVector3f;
import com.minechinima.model.proxy.BaseDataClassProxy;
import com.minechinima.model.proxy.BaseDataTreeProxy;
import com.minechinima.model.proxy.backend.api.IDataClassBackend;
import com.minechinima.model.proxy.backend.api.IDataTreeBackend;

public class Vector3fProxy extends BaseDataClassProxy implements IVector3f {
    public final ValueProxy<Float> x = new ValueProxy<>();
    public final ValueProxy<Float> y = new ValueProxy<>();
    public final ValueProxy<Float> z = new ValueProxy<>();

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
