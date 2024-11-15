package com.minechinima.model.values;

import com.minechinima.model.base.BaseValue;
import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringValue extends BaseValue<String> {
    public StringValue(String id) {
        super(id, "");
    }

    public StringValue(String id, @NotNull String defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public BaseType<?> valueToData() {
        return this.get() == null ? new StringType("") : new StringType(this.get());
    }

    @Override
    public @Nullable String valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        if (data.isString()) {
            return data.asStringType().getValue();
        }
        throw new BaseTypeCannotBeParsed("");
    }

    @Override
    public StringType toData() {
        return this.get() == null ? new StringType("") : new StringType(this.get());
    }

    @Override
    public void fromData(BaseType<?> data) {
        if (data.isString()) this.set(data.asStringType().getValue());
    }
}
