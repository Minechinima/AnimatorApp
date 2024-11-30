package com.minechinima.ui;

import com.minechinima.ui.elements.UISpace;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class UIPanelFactory {
    private final Map<String, Supplier<UISpace>> registry = new HashMap<>();

    public boolean isNameRegistered(String key) {
        return this.registry.containsKey(key);
    }

    public void registerUIPanel(String key, Supplier<UISpace> constructor) {
        if (!this.isNameRegistered(key)) this.registry.put(key, constructor);
    }

    @Nullable
    public UISpace createUIPanel(String key) {
        return this.registry.containsKey(key) ? this.registry.get(key).get() : null;
    }

    public Set<String> getRegisteredNames() {
        return this.registry.keySet();
    }
}
