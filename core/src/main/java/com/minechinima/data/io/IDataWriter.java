package com.minechinima.data.io;

import com.minechinima.data.types.BaseType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IDataWriter {
    void writeByte(byte value) throws IOException;
    void writeShort(short value) throws IOException;
    void writeInt(int value) throws IOException;
    void writeLong(long value) throws IOException;
    void writeFloat(float value) throws IOException;
    void writeDouble(double value) throws IOException;
    void writeString(String value) throws IOException;
    void writeType(byte value) throws IOException;
    void writeUUID(UUID value) throws IOException;

    default void writeByteArray(byte[] values) throws IOException {
        writeInt(values.length);
        for (byte value : values) {
            writeByte(value);
        }
    }

    default <T extends BaseType<?>> void writeList(List<T> values) throws IOException {
        writeInt(values.size());
        for (T value : values) {
            BaseType.writeToData(this, value);
        }
    }

    default void writeMap(Map<String, BaseType<?>> value) throws IOException {
        writeInt(value.size());
        for (Map.Entry<String, BaseType<?>> entry : value.entrySet()) {
            writeString(entry.getKey());
            BaseType.writeToData(this, entry.getValue());
        }
    }

    default void writeIntArray(int[] values) throws IOException {
        writeInt(values.length);
        for (int value : values) {
            writeInt(value);
        }
    }

    default void writeLongArray(long[] values) throws IOException {
        writeInt(values.length);
        for (long value : values) {
            writeLong(value);
        }
    }
}
