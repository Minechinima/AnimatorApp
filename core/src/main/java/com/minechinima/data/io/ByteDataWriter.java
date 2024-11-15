package com.minechinima.data.io;


import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

public class ByteDataWriter implements IDataWriter {
    private final DataOutput dataOutput;

    public ByteDataWriter(@NotNull DataOutput dataOutput) {
        this.dataOutput = dataOutput;
    }

    @Override
    public void writeByte(byte value) throws IOException {
        dataOutput.writeByte(value);
    }

    @Override
    public void writeShort(short value) throws IOException {
        dataOutput.writeShort(value);
    }

    @Override
    public void writeInt(int value) throws IOException {
        dataOutput.writeInt(value);
    }

    @Override
    public void writeLong(long value) throws IOException {
        dataOutput.writeLong(value);
    }

    @Override
    public void writeFloat(float value) throws IOException {
        dataOutput.writeFloat(value);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        dataOutput.writeDouble(value);
    }

    @Override
    public void writeString(String value) throws IOException {
        dataOutput.writeUTF(value);
    }

    @Override
    public void writeType(byte value) throws IOException {
        writeByte(value);
    }

    @Override
    public void writeUUID(UUID value) throws IOException {
        dataOutput.writeLong(value.getMostSignificantBits());
        dataOutput.writeLong(value.getLeastSignificantBits());
    }
}
