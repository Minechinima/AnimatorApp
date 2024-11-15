package com.minechinima.data.io.compression;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Compression {
    public static byte[] compress(int compressionAlgorithm, @NotNull byte[] data) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompressorOutputStream compressorStream = getCompressorOutputStream(compressionAlgorithm, outputStream);

        compressorStream.write(data);

        return outputStream.toByteArray();
    }

    public static byte[] decompress(int compressionAlgorithm, @NotNull byte[] data) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        CompressorInputStream compressorStream = getCompressorInputStream(compressionAlgorithm, inputStream);

        return IOUtils.toByteArray(compressorStream);
    }

    private static CompressorInputStream getCompressorInputStream(int compressionAlgorithm, @NotNull InputStream inputStream) throws IOException {
        return switch (compressionAlgorithm) {
            case 0 -> new BZip2CompressorInputStream(inputStream);
            default -> throw new IllegalArgumentException("Unknown compression algorithm: " + compressionAlgorithm);
        };
    }

    private static CompressorOutputStream getCompressorOutputStream(int compressionAlgorithm, @NotNull OutputStream inputStream) throws IOException {
        return switch (compressionAlgorithm) {
            case 0 -> new BZip2CompressorOutputStream(inputStream);
            default -> throw new IllegalArgumentException("Unknown compression algorithm: " + compressionAlgorithm);
        };
    }
}
