package com.minechinima.utils;

import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.OperationNotSupportedException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;

public class GenericRegistry<T> implements IGenericRegistry<T> {
    private final Map<Integer, Supplier<? extends T>> registry = new HashMap<>();
    private final Map<Class<? extends T>, Integer> idRegistry = new HashMap<>();

    /**
     * @param id
     * @param constructor
     * @throws OperationNotSupportedException when the given id is already registered.
     */
    @Override
    public <J extends T> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
        if (this.registry.containsKey(id)) {
            throw new IllegalArgumentException("The id is already registered");
        }

        if (this.idRegistry.containsKey(clazz)) {
            throw new IllegalArgumentException("The class is already registered");
        }

        this.registry.put(id, constructor);
        this.idRegistry.put(clazz, id);
    }

    @Override
    @Nullable
    public T getNew(int id) {
        Supplier<? extends T> constructor;
        if ((constructor = this.registry.get(id)) == null) return null;
        T config = constructor.get();
        return config;
    }

    @Override
    public Integer getID(T value) {
        return value == null ? null : this.idRegistry.get(value.getClass());
    }

    @Override
    public Map<Integer, T> getAllNew() {
        Map<Integer, T> map = new HashMap<>();
        for (Map.Entry<Integer, Supplier<? extends T>> entry : this.registry.entrySet()) {
            T config = entry.getValue().get();
            map.put(entry.getKey(), config);
        }
        return map;
    }
}
