package com.minechinima.animation.model.api;

import com.minechinima.model.api.IDataTree;
import com.minechinima.model.api.values.IDataValue;

public interface IFcurve extends IDataTree {
    IDataValue<String> dataPath();
}
