package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;

import java.io.IOException;

public class FloatType extends NumberType<Float> {
    private float value;

    protected FloatType() {
        super();
    }

    public FloatType(float value) {
        this.value = value;
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeFloat(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextFloat();
    }

    @Override
    public Float getValue() {
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
