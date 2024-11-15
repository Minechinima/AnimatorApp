package com.minechinima.data.io;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.minechinima.data.types.BaseType;

import java.io.*;

public class IOUtils {
    public static void writeToBinaryFile(BaseType<?> baseType, String path) throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path))) {
            BaseType.writeToData(new ByteDataWriter(outputStream), baseType);
        }
    }

    public static BaseType<?> readFromBinaryFile(String path) throws IOException {
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(path))) {
            return BaseType.readFromData(new ByteDataReader(dataInputStream));
        }
    }

    public static void writeToJsonFile(BaseType<?> baseType, String path) throws IOException {
        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(path))) {
            BaseType.writeToData(new GsonDataWriter(jsonWriter), baseType);
        }
    }

    public static BaseType<?> readFromJsonFile(String path) throws IOException {
        try (JsonReader jsonReader = new JsonReader(new FileReader(path))) {
            return BaseType.readFromData(new GsonDataReader(jsonReader));
        }
    }
}
