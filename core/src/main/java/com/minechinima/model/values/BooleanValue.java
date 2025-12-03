package com.minechinima.model.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.ByteType;
import com.minechinima.model.values.base.BaseValue;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import org.jetbrains.annotations.Nullable;

public class BooleanValue extends BaseValue<Boolean> {
    public BooleanValue(String id) {
        super(id, false);
    }

    public BooleanValue(String id, boolean defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public BaseType<?> valueToData(Boolean v) {
        return new ByteType(v);
    }

    @Override
    public @Nullable Boolean valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        if (data.isNumber()) {
            return data.asNumberType().boolValue();
        }
        throw new BaseTypeCannotBeParsed("");
    }

    @Override
    public ByteType toData() {
        return new ByteType(this.get());
    }

    @Override
    public void fromData(BaseType<?> data) {
        if (data.isNumber()) this.set(data.asNumberType().boolValue());
    }
}
