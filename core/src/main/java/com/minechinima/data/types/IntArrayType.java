package com.minechinima.data.types;

import com.minechinima.data.io.IDataWriter;
import com.minechinima.data.io.IDataReader;

import java.io.IOException;
import java.util.Arrays;

public class IntArrayType extends BaseType<int[]> {
    private int[] value;

    protected IntArrayType() {
    }

    public IntArrayType(int[] value) {
        this.value = Arrays.copyOf(value, value.length);
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeIntArray(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextIntArray();
    }

    @Override
    public int[] getValue() {
        return value;
    }
}
