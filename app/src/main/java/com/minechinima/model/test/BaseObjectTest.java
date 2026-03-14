package com.minechinima.model.test;

import com.minechinima.model.proxy.BaseDataTreeProxy;
import com.minechinima.model.proxy.ValueProxy;

/**
 * TODO TO TEST CLASS DECLARATIONS BEFORE PROD
 */
public class BaseObjectTest extends BaseDataTreeProxy {
    public final ValueProxy<Float> translationX = new ValueProxy<>();
    public final ValueProxy<Float> translationY = new ValueProxy<>();
    public final ValueProxy<Float> translationZ = new ValueProxy<>();
}
