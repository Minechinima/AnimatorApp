package com.minechinima.model.proxy.schema;

import com.minechinima.model.proxy.BaseDataProxy;
import com.minechinima.model.proxy.ProxyContext;

// TODO make system optional so that proxy factories can be registered in central place
//  but be overridden by per object factories e.g. operator config / hotkey stuff requires custom factories to inject registries
//  simple value lists dont require it
/**
 * separate backendbuilder and proxy factory, to allow special objects to have custom proxy factories
 * in case they require per object dependency injection context.
 * @param <T>
 */
public interface ProxyFactory<T extends BaseDataProxy<?>> {
    T createProxy(ProxyContext context);
}
