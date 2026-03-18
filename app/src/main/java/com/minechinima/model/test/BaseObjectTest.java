package com.minechinima.model.test;

import com.minechinima.animation.IAnimateable;
import com.minechinima.animation.model.AnimData;
import com.minechinima.animation.IAnimationContext;
import com.minechinima.model.api.IData;
import com.minechinima.model.proxy.BaseDataClassProxy;
import com.minechinima.model.proxy.values.ValueProxy;

/**
 * TODO TO TEST CLASS DECLARATIONS BEFORE PROD
 */
public class BaseObjectTest extends BaseDataClassProxy implements IAnimationContext {
    public final ValueProxy<Float> translationX = new ValueProxy<>();
    public final ValueProxy<Float> translationY = new ValueProxy<>();
    public final ValueProxy<Float> translationZ = new ValueProxy<>();
    public final AnimData animData = new AnimData();

    @Override
    public AnimData animData() {
        return this.animData;
    }

    public void animateX() {
        IData data = this.translationX;

        if (data instanceof IAnimateable animateable && animateable.isAnimateable()) {
            //animateable.getAnimationContext().animData().action.fcurves.add
        }
    }
}
