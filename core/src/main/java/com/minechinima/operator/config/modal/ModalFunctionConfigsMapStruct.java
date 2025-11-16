package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.IContext;
import com.minechinima.utils.GenericRegistry;
import com.minechinima.utils.IGenericRegistry;
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
public class ModalFunctionConfigsMapStruct<C extends IContext> extends BaseValueMap<ModalFunctionHotKeyConfig<C>>
        implements IGenericRegistry<ModalFunctionConfig<C>> {
    public final IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry;
    private final GenericRegistry<ModalFunctionConfig<C>> registry = new GenericRegistry<>();

    public ModalFunctionConfigsMapStruct(String id, IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry) {
        super(id);
        this.hotKeyConfigRegistry = hotKeyConfigRegistry;
    }

    @Override
    protected @NotNull ModalFunctionHotKeyConfig<C> instantiate(String id, @Nullable UUID uuid, BaseType<?> map) {
        return null;
    }

    @Override
    public @Nullable ModalFunctionConfig<C> getNew(int id) {
        return this.registry.getNew(id);
    }

    @Override
    public @Nullable Integer getID(ModalFunctionConfig<C> value) {
        return this.registry.getID(value);
    }

    @Override
    public Map<Integer, ModalFunctionConfig<C>> getAllNew() {
        return this.registry.getAllNew();
    }

    @Override
    public <J extends ModalFunctionConfig<C>> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
        this.registry.register(id, clazz, constructor);
    }
}
