package com.minechinima.data.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class ByteDataReader implements IDataReader {
    private final DataInputStream dataInputStream;

    public ByteDataReader(@NotNull DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    @Override
    public boolean hasNext() throws IOException {
        return dataInputStream.available() > 0;
    }

    @Override
    public byte readNextByte() throws IOException {
        return dataInputStream.readByte();
    }

    @Override
    public short readNextShort() throws IOException {
        return dataInputStream.readShort();
    }

    @Override
    public int readNextInt() throws IOException {
        return dataInputStream.readInt();
    }

    @Override
    public long readNextLong() throws IOException {
        return dataInputStream.readLong();
    }

    @Override
    public float readNextFloat() throws IOException {
        return dataInputStream.readFloat();
    }

    @Override
    public double readNextDouble() throws IOException {
        return dataInputStream.readDouble();
    }

    @Override
    public String readNextString() throws IOException {
        return dataInputStream.readUTF();
    }

    @Override
    public byte readNextType() throws IOException {
        return readNextByte();
    }

    @Override
    public UUID readNextUUID() throws IOException {
        long mostSigBits = dataInputStream.readLong();
        long leastSigBits = dataInputStream.readLong();

        return new UUID(mostSigBits, leastSigBits);
    }
}
