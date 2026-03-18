package com.minechinima.animation;

import com.minechinima.animation.model.AnimData;
import com.minechinima.animation.model.api.IAnimData;
import com.minechinima.model.api.IDataTree;

/**
 * Owner of an Animation data block from which animateable attributes can be fetched and animated.
 * This is a {@link IDataTree} to fetch the paths and attributes to animate from this.
 */
public interface IAnimationContext extends IDataTree {
    // TODO
    IAnimData animData();
}
