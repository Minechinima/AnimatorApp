package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;

import java.io.IOException;
import java.util.Arrays;

public class LongArrayType extends BaseType<long[]> {
    private long[] value;

    protected LongArrayType() {
    }

    public LongArrayType(long[] value) {
        this.value = Arrays.copyOf(value, value.length);
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeLongArray(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextLongArray();
    }

    @Override
    public long[] getValue() {
        return value;
    }
}
