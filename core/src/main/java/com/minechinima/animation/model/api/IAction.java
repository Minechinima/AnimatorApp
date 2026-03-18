package com.minechinima.animation.model.api;

import com.minechinima.model.api.IDataTree;
import com.minechinima.model.api.values.IDataMap;

public interface IAction extends IDataTree {
    IDataMap<? extends IFcurve> fcurves();
}
