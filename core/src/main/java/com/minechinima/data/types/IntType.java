package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;

import java.io.IOException;

public class IntType extends NumberType<Integer> {
    private int value;

    protected IntType() {
        super();
    }

    public IntType(int value) {
        this.value = value;
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeInt(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextInt();
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public int intValue() {
        return this.getValue().intValue();
    }

    @Override
    public float floatValue() {
        return this.getValue().floatValue();
    }

    @Override
    public long longValue() {
        return this.getValue().longValue();
    }

    @Override
    public double doubleValue() {
        return this.getValue().doubleValue();
    }
}
