package com.minechinima.data.io;

import com.google.gson.stream.JsonReader;
import com.minechinima.data.types.BaseType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class GsonDataReader implements IDataReader {
    private final JsonReader reader;

    public GsonDataReader(@NotNull JsonReader reader) throws IOException {
        this.reader = reader;
        this.reader.beginObject();
    }

    @Override
    public boolean hasNext() throws IOException {
        return reader.hasNext();
    }

    @Override
    public byte readNextByte() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        byte value = (byte) reader.nextInt();
        reader.endObject();

        return value;
    }

    @Override
    public short readNextShort() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        short value = (short) reader.nextInt();
        reader.endObject();

        return value;
    }

    @Override
    public int readNextInt() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        int value = reader.nextInt();
        reader.endObject();

        return value;
    }

    @Override
    public long readNextLong() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        long value = reader.nextLong();
        reader.endObject();

        return value;
    }

    @Override
    public float readNextFloat() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        float value = (float) reader.nextDouble();
        reader.endObject();

        return value;
    }

    @Override
    public double readNextDouble() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        double value = reader.nextDouble();
        reader.endObject();

        return value;
    }

    @Override
    public String readNextString() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        String value = reader.nextString();
        reader.endObject();

        return value;
    }

    @Override
    public byte readNextType() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("type");

        return (byte) reader.nextInt();
    }

    @Override
    public UUID readNextUUID() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        long[] array = new long[2];

        reader.beginArray();
        int i = 0;
        while (reader.hasNext()) {
            array[i] = reader.nextLong();
            i++;
        }
        reader.endArray();
        reader.endObject();

        return new UUID(array[0], array[1]);
    }

    @Override
    public byte[] readNextByteArray() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("size");

        int size = reader.nextInt();
        byte[] array = new byte[size];

        key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        reader.beginArray();
        int i = 0;
        while (reader.hasNext()) {
            array[i] = (byte) reader.nextInt();
            i++;
        }
        reader.endArray();
        reader.endObject();

        return array;
    }

    @Override
    public int[] readNextIntArray() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("size");

        int size = reader.nextInt();
        int[] array = new int[size];

        key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        reader.beginArray();
        int i = 0;
        while (reader.hasNext()) {
            array[i] = reader.nextInt();
            i++;
        }
        reader.endArray();
        reader.endObject();

        return array;
    }

    @Override
    public List<BaseType<?>> readNextList() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("size");

        int length = reader.nextInt();
        ArrayList<BaseType<?>> list = new ArrayList<>(length);

        key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        reader.beginArray();
        for (int i = 0; i < length; i++) {
            reader.beginObject();
            list.add(BaseType.readFromData(this));
        }
        reader.endArray();
        reader.endObject();

        return list;
    }

    @Override
    public long[] readNextLongArray() throws IOException {
        String key = reader.nextName();
        assert key.equalsIgnoreCase("size");

        int size = reader.nextInt();
        long[] array = new long[size];

        key = reader.nextName();
        assert key.equalsIgnoreCase("value");

        reader.beginArray();
        int i = 0;
        while (reader.hasNext()) {
            array[i] = reader.nextLong();
            i++;
        }
        reader.endArray();
        reader.endObject();

        return array;
    }

    @Override
    public Map<String, BaseType<?>> readNextMap() throws IOException {
        String outerKey = reader.nextName();
        assert outerKey.equalsIgnoreCase("value");

        HashMap<String, BaseType<?>> map = new HashMap<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();

            reader.beginObject();
            BaseType<?> value = BaseType.readFromData(this);

            map.put(key, value);
        }
        reader.endObject();
        reader.endObject();

        return map;
    }
}
