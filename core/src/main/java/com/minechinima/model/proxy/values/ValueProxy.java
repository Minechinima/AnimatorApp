package com.minechinima.model.proxy.values;

import com.minechinima.animation.IAnimateable;
import com.minechinima.animation.IAnimationContext;
import com.minechinima.model.api.values.IDataValue;
import com.minechinima.model.proxy.BaseDataProxy;
import com.minechinima.model.proxy.backend.api.BackendLayer;
import com.minechinima.model.proxy.backend.api.IDataValueBackend;
import org.jetbrains.annotations.Nullable;

public class ValueProxy<V> extends BaseDataProxy<IDataValueBackend<V>> implements IDataValue<V>, IAnimateable {
    @Override
    public @Nullable V get() {
        return this.backend.get();
    }

    public void set(@Nullable V value, BackendLayer layer) {
        IDataValueBackend<V> backend = this.backendLayers.getHandlerForLayer(layer);
        if (backend != null) backend.set(value);
    }

    @Override
    public @Nullable V getDefaultValue() {
        return this.backend.getDefaultValue();
    }

    @Override
    public boolean isAnimateable() {
        return this.backendLayers.getHandlerForLayer(BackendLayer.ANIMATED) != null;
    }

    @Override
    public IAnimationContext getAnimationContext() {
        return null;
    }
}
