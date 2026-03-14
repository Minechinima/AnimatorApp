package com.minechinima.model.api.values;

import com.minechinima.model.api.IData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface IDataMap<T extends IData> {
    int size();
    boolean isEmpty();
    boolean containsKey(String key);
    boolean containsUUID(UUID uuid);
    boolean containsValue(Object value);
    T get(String key);
    T get(UUID uuid);
}
