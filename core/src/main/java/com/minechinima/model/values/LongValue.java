package com.minechinima.model.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.LongType;
import com.minechinima.data.types.NumberType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseValueNumber;
import org.jetbrains.annotations.Nullable;

public class LongValue extends BaseValueNumber<Long> {
    public LongValue(String id, long defaultValue, long min, long max) {
        super(id, defaultValue, min, max);
    }

    public LongValue(String id, long defaultValue) {
        super(id, defaultValue);
    }

    public LongValue(String id) {
        super(id, 0L);
    }

    @Override
    public BaseType<?> valueToData(Long v) {
        return new LongType(v);
    }

    @Override
    public @Nullable Long valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        if (data.isNumber()) {
            return data.asNumberType().longValue();
        }
        throw new BaseTypeCannotBeParsed("");
    }

    @Override
    public void fromData(BaseType<?> data) {
        NumberType<?> type;
        if ((type = data.asNumberType()) == null) return;
        this.set(type.longValue());
    }

    @Override
    public LongType toData() {
        return new LongType(this.get());
    }
}
