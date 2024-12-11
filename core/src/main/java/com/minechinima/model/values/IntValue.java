package com.minechinima.model.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.IntType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseValueNumber;
import org.jetbrains.annotations.Nullable;

public class IntValue extends BaseValueNumber<Integer> {
    public IntValue(String id) {
        super(id, 0);
    }

    public IntValue(String id, int defaultValue) {
        super(id, defaultValue);
    }

    public IntValue(String id, int defaultValue, int max, int min) {
        super(id, defaultValue, max, min);
    }

    @Override
    public BaseType<?> valueToData() {
        return new IntType(this.get());
    }

    @Override
    public @Nullable Integer valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        if (data.isNumber()) {
            return data.asNumberType().intValue();
        }
        throw new BaseTypeCannotBeParsed("");
    }

    @Override
    public IntType toData() {
        return new IntType(this.get());
    }

    @Override
    public void fromData(BaseType<?> data) {
        if (data.isNumber()) this.set(data.asNumberType().intValue());
    }
}
