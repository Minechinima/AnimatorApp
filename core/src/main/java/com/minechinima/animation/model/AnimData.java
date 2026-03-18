package com.minechinima.animation.model;

import com.minechinima.animation.model.api.IAction;
import com.minechinima.animation.model.api.IAnimData;
import com.minechinima.model.proxy.BaseDataClassProxy;

public class AnimData extends BaseDataClassProxy implements IAnimData {
    public final Action action = new Action();

    @Override
    public IAction action() {
        return this.action;
    }
}
