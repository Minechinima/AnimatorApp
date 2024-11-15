package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;

import java.io.IOException;

public class DoubleType extends NumberType<Double> {
    private double value;

    protected DoubleType() {
        super();
    }

    public DoubleType(double value) {
        this.value = value;
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeDouble(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextDouble();
    }

    @Override
    public Double getValue() {
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
