package com.minechinima.utils;

import com.minechinima.data.types.ByteArrayType;
import com.minechinima.data.types.MapType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtils {
    /**
     * Converts the given UUID to a byte array, where the first eight bytes are the UUID's most significant 64 bits,
     * and the last eight bytes are the least significant 64 bits of the UUID.
     * @param uuid
     * @return a byte array
     */
    public static byte[] convertUUIDToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Tries to convert the given byte array into a UUID.
     * @param bytes
     * @return the uuid converted from the byte array.
     * @throws IOException when there are less than 16 bytes or the bytes cannot be converted to a UUID
     */
    public static UUID convertBytesToUUID(byte[] bytes) throws IOException {
        if (bytes.length < 16) {
            throw new IOException("Cannot convert byte array with less than 16 bytes to UUID");
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    /**
     * @return the data with the uuid inserted as bytearray.
     */
    public static MapType putUUID(MapType data, String key, UUID uuid) {
        //TODO deprecate and use UUIDType
        data.getValue().put(key, new ByteArrayType(UUIDUtils.convertUUIDToBytes(uuid)));
        return data;
    }

    /**
     * @param data
     * @param key
     * @return the parsed uuid or null if it was not present.
     * @throws BaseTypeCannotBeParsed if the uuid could not be created but the key was present with the correct datatype.
     */
    @Nullable
    public static UUID readUUIDorNull(MapType data, String key) throws BaseTypeCannotBeParsed {
        if (data.getByteArrayType(key) == null) {
            return null;
        }

        try {
            return UUIDUtils.convertBytesToUUID(data.getByteArrayType(key).getValue());
        } catch (IOException e) {
            throw new BaseTypeCannotBeParsed(e);
        }
    }

    /**
     * @param data
     * @return the uuid read from the data using the key or creating a random uuid if not present
     * or if an error occurred and the bytes cannot be converted.
     */
    public static UUID readUUIDOrCreateRandom(MapType data, String key) {
        ByteArrayType array;
        if ((array = data.getByteArrayType(key)) == null) return UUID.randomUUID();
        if (array.getValue().length < 16) {
            return UUID.randomUUID();
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(array.getValue());
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
