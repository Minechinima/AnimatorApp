package com.minechinima.utils;

public class BitOperationUtils {
    public static boolean isBitFlagSet(int flagToTest, int flags) {
        return (flagToTest & flags) == flagToTest;
    }

    public static long MSBBytesToLong(byte[] arr) {
        if (arr.length < 8) throw new IllegalArgumentException("Byte array has less than 8 bytes.");

        long val = 0;
        for (int i = 0; i < 8; i++) {
            val = (val << 8) + (arr[i] & 0xFFL);
        }

        return val;
    }

    public static long LSBBytesToLong(byte[] arr) {
        if (arr.length < 8) throw new IllegalArgumentException("Byte array has less than 8 bytes.");

        long val = 0;
        for (int i = 0; i < 8; i++) {
            val += ((long) arr[i] & 0xFFL) << (8 * i);
        }

        return val;
    }
}
