package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;

import java.io.IOException;

public class ByteType extends NumberType<Byte> {
    private byte value = 0;

    protected ByteType() {
        super();
    }

    public ByteType(byte value) {
        this.value = value;
    }

    public ByteType(boolean value) {
        this.value = (byte) (value ? 1 : 0);
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeByte(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextByte();
    }

    @Override
    public Byte getValue() {
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
