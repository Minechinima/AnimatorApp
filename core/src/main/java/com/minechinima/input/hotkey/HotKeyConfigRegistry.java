package com.minechinima.input.hotkey;

import com.minechinima.utils.IGenericDefaultRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.OperationNotSupportedException;
import java.util.*;
import java.util.function.Supplier;

/**
 * Registry and factory for available {@link HotKeyConfigGroupStruct} types.
 */
public class HotKeyConfigRegistry implements IGenericDefaultRegistry<HotKeyConfigGroupStruct> {
    private final Map<Integer, Supplier<? extends HotKeyConfigGroupStruct>> registry = new HashMap<>();
    private final Map<Class<? extends HotKeyConfigGroupStruct>, Integer> idRegistry = new HashMap<>();
    private final Supplier<? extends HotKeyConfigGroupStruct> defaultConfig;

    public <T extends HotKeyConfigGroupStruct> HotKeyConfigRegistry(int id, Class<T> clazz, Supplier<T> defaultConfig) {
        this.register(id, clazz, defaultConfig);
        this.defaultConfig = defaultConfig;
    }

    /**
     * @param id
     * @param constructor
     * @throws OperationNotSupportedException when the given id is already registered.
     */
    @Override
    public <T extends HotKeyConfigGroupStruct> void register(int id, Class<T> clazz, Supplier<T> constructor) throws IllegalArgumentException {
        if (this.registry.containsKey(id) || this.idRegistry.containsKey(clazz)) {
            throw new IllegalArgumentException("The id is already registered");
        }

        this.registry.put(id, constructor);
        this.idRegistry.put(clazz, id);
    }

    @Override
    @Nullable
    public HotKeyConfigGroupStruct getNew(int id) {
        Supplier<? extends HotKeyConfigGroupStruct> constructor;
        if ((constructor = this.registry.get(id)) == null) return null;
        HotKeyConfigGroupStruct config = constructor.get();
        config.initChildren();
        return config;
    }

    @Override
    public Integer getID(HotKeyConfigGroupStruct value) {
        return this.idRegistry.get(value.getClass());
    }

    @Override
    public Map<Integer, HotKeyConfigGroupStruct> getAllNew() {
        Map<Integer, HotKeyConfigGroupStruct> map = new HashMap<>();
        for (Map.Entry<Integer, Supplier<? extends HotKeyConfigGroupStruct>> entry : this.registry.entrySet()) {
            HotKeyConfigGroupStruct config = entry.getValue().get();
            config.initChildren();
            map.put(entry.getKey(), config);
        }
        return map;
    }

    @Override
    @NotNull
    public HotKeyConfigGroupStruct getNewDefault() {
        HotKeyConfigGroupStruct defaultconfig = this.defaultConfig.get();
        defaultconfig.initChildren();
        return defaultconfig;
    }
}
