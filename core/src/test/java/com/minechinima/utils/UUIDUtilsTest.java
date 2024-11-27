package com.minechinima.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class UUIDUtilsTest {
    @Test
    public void convertUUIDToBytes() {
        UUID rand = UUID.randomUUID();

        byte[] arr = UUIDUtils.convertUUIDToBytes(rand);
        long mostSignificant = BitOperationUtils.MSBBytesToLong(arr);
        long leastSignificant = BitOperationUtils.MSBBytesToLong(Arrays.copyOfRange(arr, 8, 16));

        Assertions.assertEquals(rand, new UUID(mostSignificant, leastSignificant));
    }

    @Test
    public void convertBytesToUUID() throws IOException {
        Supplier<Byte> randByte = () -> (byte) ((Math.random() < 0.5 ? -1 : 1) * Math.random() * 0xFF);
        byte[] bytes = new byte[16];
        for (int i = 0; i < bytes.length; i++) {
            bytes[0] = randByte.get();
        }

        long mostSignificant = BitOperationUtils.MSBBytesToLong(bytes);
        long leastSignificant = BitOperationUtils.MSBBytesToLong(Arrays.copyOfRange(bytes, 8, 16));
        Assertions.assertEquals(UUIDUtils.convertBytesToUUID(bytes), new UUID(mostSignificant, leastSignificant));
    }
}
