package com.minechinima.model;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.AbstractEnumValue;
import com.minechinima.ui.utils.ILangKeyProvider;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.IGenericRegistry;
import com.minechinima.utils.RegistryUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class OrientationEnumValueStruct extends AbstractEnumValue<OrientationEnumValueStruct.Orientation> {
    public OrientationEnumValueStruct(String id, Orientation defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData(this.get());
    }

    @Override
    public void fromData(BaseType<?> data) {
        Orientation v = RegistryUtils.deserialize(data, Orientation.REGISTRY);
        if (v != null) this.set(v);
    }

    @Override
    public BaseType<?> valueToData(Orientation obj) {
        return RegistryUtils.serialize(Orientation.REGISTRY, obj);
    }

    @Override
    public @Nullable Orientation valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        Orientation v = RegistryUtils.deserialize(data, Orientation.REGISTRY);
        if (v == null) throw new BaseTypeCannotBeParsed("");
        return v;
    }

    @Override
    public Map<Integer, Orientation> getAvailableValues() {
        return CollectionUtils.toMap(Orientation.values());
    }

    @Override
    public Integer getCurrentValueIndex() {
        return this.get().ordinal();
    }

    public enum Orientation implements ILangKeyProvider {
        GLOBAL,
        LOCAL;

        public static final IGenericRegistry<Orientation> REGISTRY = new IGenericRegistry<Orientation>() {
            @Nullable
            @Override
            public Orientation getNew(int id) {
                return Orientation.values()[id];
            }

            @Nullable
            @Override
            public Integer getID(Orientation value) {
                return value.ordinal();
            }

            @Override
            public Map<Integer, Orientation> getAllNew() {
                return CollectionUtils.toMap(Orientation.values());
            }

            @Override
            public <J extends Orientation> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
        };

        @Override
        public String title() {
            return this.name();
        }
    }
}
