package com.minechinima.data.io;

import com.google.gson.stream.JsonWriter;
import com.minechinima.data.types.BaseType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GsonDataWriter implements IDataWriter {
    private final JsonWriter writer;

    public GsonDataWriter(@NotNull JsonWriter writer) throws IOException {
        this.writer = writer;
        this.writer.beginObject();
    }

    @Override
    public void writeByte(byte value) throws IOException {
        writer.name("value").value(value).endObject();
    }

    @Override
    public void writeShort(short value) throws IOException {
        writer.name("value").value(value).endObject();
    }

    @Override
    public void writeInt(int value) throws IOException {
        writer.name("value").value(value).endObject();
    }

    @Override
    public void writeLong(long value) throws IOException {
        writer.name("value").value(value).endObject();
    }

    @Override
    public void writeFloat(float value) throws IOException {
        writer.name("value").value(value).endObject();
    }

    @Override
    public void writeDouble(double value) throws IOException {
        writer.name("value").value(value).endObject();
    }

    @Override
    public void writeString(String value) throws IOException {
        writer.name("value").value(value).endObject();
    }

    @Override
    public void writeType(byte value) throws IOException {
        writer.name("type").value(value);
    }

    @Override
    public void writeUUID(UUID value) throws IOException {
        writer.name("value").beginArray()
                .value(value.getMostSignificantBits())
                .value(value.getLeastSignificantBits())
                .endArray()
                .endObject();
    }

    @Override
    public void writeByteArray(byte[] values) throws IOException {
        writer.name("size").value(values.length);

        writer.name("value").beginArray();
        for (byte value : values) {
            writer.value(value);
        }
        writer.endArray().endObject();
    }

    @Override
    public void writeIntArray(int[] values) throws IOException {
        writer.name("size").value(values.length);

        writer.name("value").beginArray();
        for (int value : values) {
            writer.value(value);
        }
        writer.endArray().endObject();
    }

    @Override
    public <T extends BaseType<?>> void writeList(List<T> values) throws IOException {
        writer.name("size").value(values.size());

        writer.name("value").beginArray();
        for (T value : values) {
            writer.beginObject();
            BaseType.writeToData(this, value);
        }
        writer.endArray().endObject();
    }

    @Override
    public void writeLongArray(long[] values) throws IOException {
        writer.name("size").value(values.length);

        writer.name("value").beginArray();
        for (long value : values) {
            writeLong(value);
        }
        writer.endArray().endObject();
    }

    @Override
    public void writeMap(Map<String, BaseType<?>> value) throws IOException {
        writer.name("value").beginObject();
        for (Map.Entry<String, BaseType<?>> entry : value.entrySet()) {
            writer.name(entry.getKey());

            writer.beginObject();
            BaseType.writeToData(this, entry.getValue());
        }
        writer.endObject().endObject();
    }
}
