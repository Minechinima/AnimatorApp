package com.minechinima.animation.model;

import com.minechinima.animation.model.api.IAction;
import com.minechinima.animation.model.api.IFcurve;
import com.minechinima.model.api.values.IDataMap;
import com.minechinima.model.proxy.BaseDataClassProxy;
import com.minechinima.model.proxy.values.MapProxy;

public class Action extends BaseDataClassProxy implements IAction {
    public final MapProxy<Fcurve> fcurves = new MapProxy<>();

    @Override
    public IDataMap<? extends IFcurve> fcurves() {
        return this.fcurves;
    }
}
