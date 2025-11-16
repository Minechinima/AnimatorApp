package com.minechinima.model;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.configs.values.MouseKeyEnum;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.AbstractEnumValue;
import com.minechinima.ui.utils.ILangKeyProvider;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.IGenericRegistry;
import com.minechinima.utils.RegistryUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class AxisEnumValueStruct extends AbstractEnumValue<AxisEnumValueStruct.Axis> {
    public AxisEnumValueStruct(String id, Axis defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData();
    }

    @Override
    public void fromData(BaseType<?> data) {
        AxisEnumValueStruct.Axis v = RegistryUtils.deserialize(data, AxisEnumValueStruct.Axis.REGISTRY);
        if (v != null) this.set(v);
    }

    @Override
    public BaseType<?> valueToData() {
        return RegistryUtils.serialize(AxisEnumValueStruct.Axis.REGISTRY, this.get());
    }

    @Override
    public @Nullable AxisEnumValueStruct.Axis valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        AxisEnumValueStruct.Axis v = RegistryUtils.deserialize(data, AxisEnumValueStruct.Axis.REGISTRY);
        if (v == null) throw new BaseTypeCannotBeParsed("");
        return v;
    }

    @Override
    public Map<Integer, AxisEnumValueStruct.Axis> getAvailableValues() {
        return CollectionUtils.toMap(AxisEnumValueStruct.Axis.values());
    }

    @Override
    public Integer getCurrentValueIndex() {
        return this.get().ordinal();
    }

    public enum Axis implements ILangKeyProvider {
        X,
        Y,
        Z;

        public static final IGenericRegistry<AxisEnumValueStruct.Axis> REGISTRY = new IGenericRegistry<Axis>() {
            @Nullable
            @Override
            public Axis getNew(int id) {
                return AxisEnumValueStruct.Axis.values()[id];
            }

            @Nullable
            @Override
            public Integer getID(Axis value) {
                return value.ordinal();
            }

            @Override
            public Map<Integer, Axis> getAllNew() {
                return CollectionUtils.toMap(AxisEnumValueStruct.Axis.values());
            }

            @Override
            public <J extends Axis> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
        };

        @Override
        public String title() {
            return this.name();
        }
    }
}
