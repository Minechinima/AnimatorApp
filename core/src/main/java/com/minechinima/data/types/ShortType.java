package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;

import java.io.IOException;

public class ShortType extends NumberType<Short> {
    private short value;

    protected ShortType() {
        super();
    }

    public ShortType(short value) {
        this.value = value;
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeShort(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextShort();
    }

    @Override
    public Short getValue() {
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
