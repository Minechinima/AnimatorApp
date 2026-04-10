package com.minechinima.animation.model;

import com.minechinima.animation.model.api.IFcurve;
import com.minechinima.model.api.values.IDataValue;
import com.minechinima.model.proxy.BaseDataClassProxy;
import com.minechinima.model.proxy.values.ValueProxy;

public class Fcurve extends BaseDataClassProxy implements IFcurve {
    public final ValueProxy<String> dataPath = new ValueProxy<>();

    @Override
    public IDataValue<String> dataPath() {
        return this.dataPath;
    }
}
