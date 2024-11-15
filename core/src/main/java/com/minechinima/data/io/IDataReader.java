package com.minechinima.data.io;

import com.minechinima.data.types.BaseType;

import java.io.IOException;
import java.util.*;

public interface IDataReader {
    boolean hasNext() throws IOException;
    byte readNextByte() throws IOException;
    short readNextShort() throws IOException;
    int readNextInt() throws IOException;
    long readNextLong() throws IOException;
    float readNextFloat() throws IOException;
    double readNextDouble() throws IOException;
    String readNextString() throws IOException;
    byte readNextType() throws IOException;
    UUID readNextUUID() throws IOException;

    default List<BaseType<?>> readNextList() throws IOException {
        int length = readNextInt();
        ArrayList<BaseType<?>> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            list.add(BaseType.readFromData(this));
        }

        return list;
    }

    default Map<String, BaseType<?>> readNextMap() throws IOException {
        int length = readNextInt();
        HashMap<String, BaseType<?>> map = new HashMap<>();

        for (int i = 0; i < length; i++) {
            String key = readNextString();
            map.put(key, BaseType.readFromData(this));
        }

        return map;
    }

    default byte[] readNextByteArray() throws IOException {
        int length = readNextInt();
        byte[] array = new byte[length];

        for (int i = 0; i < length; i++) {
            array[i] = readNextByte();
        }

        return array;
    }

    default int[] readNextIntArray() throws IOException {
        int length = readNextInt();
        int[] array = new int[length];

        for (int i = 0; i < length; i++) {
            array[i] = readNextInt();
        }

        return array;
    }

    default long[] readNextLongArray() throws IOException {
        int length = readNextInt();
        long[] array = new long[length];

        for (int i = 0; i < length; i++) {
            array[i] = readNextLong();
        }

        return array;
    }
}
