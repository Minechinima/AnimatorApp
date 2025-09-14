package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ModalFunctionGroupStruct;
import com.minechinima.utils.GenericRegistry;
import com.minechinima.utils.IGenericDefaultRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Map of global modal function configuration objects.
 * <br><br>
 * @param <C>
 * @implSpec is a map to retrieve modal functions by a key for easier runtime read/write access.
 */
public class ModalFunctionConfigsMapStruct<C extends IContext> extends BaseValueMap<ModalFunctionConfigGroupStruct<C>>
        implements IGenericDefaultRegistry<ModalFunctionGroupStruct<C>> {
    public final HotKeyConfigRegistry hotKeyConfigRegistry;
    private final GenericRegistry<ModalFunctionGroupStruct<C>> registry = new GenericRegistry<>();

    public ModalFunctionConfigsMapStruct(String id, HotKeyConfigRegistry hotKeyConfigRegistry) {
        super(id);
        this.hotKeyConfigRegistry = hotKeyConfigRegistry;
    }

    @Override
    protected @NotNull ModalFunctionConfigGroupStruct<C> instantiate(String id, @Nullable UUID uuid, BaseType<?> map) {
        return null;
    }

    @Override
    public @NotNull ModalFunctionGroupStruct<C> getNewDefault() {
        return null;
    }

    @Override
    public @Nullable ModalFunctionGroupStruct<C> getNew(int id) {
        return this.registry.getNew(id);
    }

    @Override
    public @Nullable Integer getID(ModalFunctionGroupStruct<C> value) {
        return this.registry.getID(value);
    }

    @Override
    public Map<Integer, ModalFunctionGroupStruct<C>> getAllNew() {
        return this.registry.getAllNew();
    }

    @Override
    public <J extends ModalFunctionGroupStruct<C>> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
        this.registry.register(id, clazz, constructor);
    }
}
