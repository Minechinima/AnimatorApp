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

public class AxisConstraintEnumValueStruct extends AbstractEnumValue<AxisConstraintEnumValueStruct.AxisConstraint> {
    public AxisConstraintEnumValueStruct(String id) {
        super(id, AxisConstraint.ALL);
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData(this.get());
    }

    @Override
    public void fromData(BaseType<?> data) {
        AxisConstraint v = RegistryUtils.deserialize(data, AxisConstraint.REGISTRY);
        if (v != null) this.set(v);
    }

    @Override
    public BaseType<?> valueToData(AxisConstraint obj) {
        return RegistryUtils.serialize(AxisConstraint.REGISTRY, obj);
    }

    @Override
    public @Nullable AxisConstraint valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        AxisConstraint v = RegistryUtils.deserialize(data, AxisConstraint.REGISTRY);
        if (v == null) throw new BaseTypeCannotBeParsed("");
        return v;
    }

    @Override
    public Map<Integer, AxisConstraint> getAvailableValues() {
        return CollectionUtils.toMap(AxisConstraint.values());
    }

    @Override
    public Integer getCurrentValueIndex() {
        return this.get().ordinal();
    }

    public enum AxisConstraint implements ILangKeyProvider {
        ALL,
        X,
        Y,
        Z,
        XY,
        XZ,
        YZ;

        public static final IGenericRegistry<AxisConstraint> REGISTRY = new IGenericRegistry<>() {
            @Nullable
            @Override
            public AxisConstraint getNew(int id) {
                return AxisConstraint.values()[id];
            }

            @Nullable
            @Override
            public Integer getID(AxisConstraint value) {
                return value.ordinal();
            }

            @Override
            public Map<Integer, AxisConstraint> getAllNew() {
                return CollectionUtils.toMap(AxisConstraint.values());
            }

            @Override
            public <J extends AxisConstraint> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
        };

        @Override
        public String title() {
            return this.name();
        }
    }
}
