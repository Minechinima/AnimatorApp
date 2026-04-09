package com.minechinima.model.proxy.values;

import com.minechinima.model.api.values.IDataValue;
import com.minechinima.model.api.values.vectors.IVector3f;
import com.minechinima.model.proxy.BaseDataClassProxy;
import com.minechinima.model.proxy.schema.AnnotationExpand;
import com.minechinima.model.proxy.schema.ProxyField;
// declares which attributes of this class can be animated, when this is used as an attribute in another class
//@AnimateableAttributes(attributes = {"x", "y", "z"})
public class Vector3fProxy extends BaseDataClassProxy implements IVector3f {
    @ProxyField(key = "x")
    @ValueType(Float.class)
    public final ValueProxy<Float> x = new ValueProxy<>();

    @ProxyField(key = "y")
    @ValueType(Float.class)
    public final ValueProxy<Float> y = new ValueProxy<>();

    @ProxyField(key = "z")
    @ValueType(Float.class)
    public final ValueProxy<Float> z = new ValueProxy<>();

    // TODO test
    @ProxyField(key = "test")
    @CollectionItemFactory(@AnnotationExpand(ValueProxy.class))
    @ValueType(Float.class)
    public final MapProxy<ValueProxy<Float>> test = new MapProxy<>();

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
