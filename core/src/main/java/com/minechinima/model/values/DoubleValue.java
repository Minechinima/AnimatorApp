package com.minechinima.model.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.DoubleType;
import com.minechinima.data.types.NumberType;
import com.minechinima.model.values.base.BaseValueNumber;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;

public class DoubleValue extends BaseValueNumber<Double> {

    public DoubleValue(String id, double defaultValue, double min, double max) {
        super(id, defaultValue, min, max);
    }

    public DoubleValue(String id, double defaultValue) {
        super(id, defaultValue);
    }

    public DoubleValue(String id) {
        super(id, 0D);
    }

    @Override
    public BaseType<?> valueToData() {
        return new DoubleType(this.get());
    }

    @Override
    public Double valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        if (data.isNumber()) {
            return data.asNumberType().doubleValue();
        }
        throw new BaseTypeCannotBeParsed("");
    }

    @Override
    public DoubleType toData() {
        return new DoubleType(this.get());
    }

    @Override
    public void fromData(BaseType<?> data) {
        NumberType<?> type;
        if ((type = data.asNumberType()) == null) return;
        this.set(type.doubleValue());
    }
}
