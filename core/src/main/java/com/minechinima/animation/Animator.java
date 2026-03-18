package com.minechinima.animation;

import com.minechinima.animation.model.Fcurve;
import com.minechinima.animation.model.api.IFcurve;
import com.minechinima.model.api.IData;
import com.minechinima.model.exceptions.PathParsingException;
import com.minechinima.model.proxy.values.ValueProxy;
import com.minechinima.model.utils.ValuePath;

//TODO this is just temporary test to test Proxy, backend layers and animation capabalitieis
public class Animator {
    private final IAnimationContext context;


    public Animator(IAnimationContext context) {
        this.context = context;
    }

    public void test() {
        for (IFcurve fcurve : this.context.animData().action().fcurves().values()) {
            try {
                IData attribute = this.context.getChildByUniquePath(new ValuePath(fcurve.dataPath().get()));

                if (attribute instanceof IAnimateable animateable && animateable.isAnimateable()) {
                    this.animateGenericValue(animateable);
                }
            } catch (PathParsingException e) {
                e.printStackTrace();
            }
        }
    }

    public void animateGenericValue(IAnimateable animateable) {
        //TODO this will be a registry that can animate any value. This needs to be generic, as besides
        // values with setters, there are also datastructures with complex setters like lists
        if (animateable instanceof ValueProxy<?> proxy) {
            //proxy.set(null, BackendLayer.ANIMATED);
        }
    }
}
