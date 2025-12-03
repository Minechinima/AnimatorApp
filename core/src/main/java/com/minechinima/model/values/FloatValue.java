package com.minechinima.model.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.FloatType;
import com.minechinima.data.types.NumberType;
import com.minechinima.model.values.base.BaseValueNumber;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import org.jetbrains.annotations.Nullable;

public class FloatValue extends BaseValueNumber<Float> {
    public FloatValue(String id, float defaultValue, float min, float max) {
        super(id, defaultValue, min, max);
    }

    public FloatValue(String id, float defaultValue) {
        super(id, defaultValue);
    }

    public FloatValue(String id) {
        super(id, 0F);
    }

    @Override
    public BaseType<?> valueToData(Float v) {
        return new FloatType(v);
    }

    @Override
    public @Nullable Float valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        if (data.isNumber()) {
            return data.asNumberType().floatValue();
        }
        throw new BaseTypeCannotBeParsed("");
    }

    @Override
    public FloatType toData() {
        return new FloatType(this.get());
    }

    @Override
    public void fromData(BaseType<?> data) {
        NumberType<?> type;
        if ((type = data.asNumberType()) == null) return;
        this.set(type.floatValue());
    }
}
