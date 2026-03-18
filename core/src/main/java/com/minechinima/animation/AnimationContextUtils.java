package com.minechinima.animation;

import com.minechinima.model.api.IData;
import com.minechinima.model.api.IDataTree;
import org.jetbrains.annotations.Nullable;

public class AnimationContextUtils {
    @Nullable
    public static IAnimationContext getAnimationContext(IAnimateable animateable) {
        if (animateable instanceof IAnimationContext ctx) return ctx;

        if (animateable instanceof IData data) {
            IData parent = data;

            while ((parent = parent.getParent()) != null) {
                if (parent instanceof IAnimationContext context) return context;
            }
        }

        return null;
    }
}
