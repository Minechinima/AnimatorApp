package com.minechinima.data.io.file;

import com.minechinima.data.io.compression.Compression;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class CompressedFileBlock extends FileBlock {
    public CompressedFileBlock(@NotNull Path path) throws IOException {
        super(path);
    }

    public CompressedFileBlock(@NotNull File file) throws IOException {
        super(file);
    }

    public CompressedFileBlock(byte @NotNull [] data) throws IOException {
        super(data);
    }

    @Override
    protected byte[] getBodyBytesForRead() throws IOException {
        return Compression.decompress(getCompressionAlgorithm(), super.getBodyBytesForRead());
    }

    @Override
    protected byte[] getBodyBytesForWrite() throws IOException {
        return Compression.compress(getCompressionAlgorithm(), getBody());
    }
}
