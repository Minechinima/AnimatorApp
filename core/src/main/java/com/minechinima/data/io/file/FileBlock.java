package com.minechinima.data.io.file;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileBlock {
    private Path path;
    private static final int HEADER_SIZE = 1024;

    protected byte[] data = new byte[1024];
    private int version = 0;
    private int compressionAlgorithm = 0;
    private int contentType = 0;
    private int contentLength = 0;
    private byte[] body = new byte[0];

    protected FileBlock(@NotNull Path path) throws IOException {
        this(Files.readAllBytes(path));
        this.path = path;
    }

    protected FileBlock(@NotNull File file) throws IOException {
        this(file.toPath());
    }

    protected FileBlock(byte @NotNull [] data) {
        this.data = data;
    }

    public void parse() throws IOException {
        parseHeader(getHeaderBytes());
        parseBody(getBodyBytesForRead());
    }

    private void parseHeader(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        version = byteBuffer.getInt();
        compressionAlgorithm = byteBuffer.getInt();
        contentType = byteBuffer.getInt();
        contentLength = byteBuffer.getInt();
    }

    protected byte[] getHeaderBytes() {
        return Arrays.copyOfRange(data, 0, HEADER_SIZE);
    }

    protected void parseBody(byte[] bytes) {
        body = bytes;
    }

    protected byte[] getBodyBytesForRead() throws IOException {
        return Arrays.copyOfRange(data, HEADER_SIZE, contentLength);
    }

    public void write() throws IOException {
        byte[] bodyBytes = getBodyBytesForWrite();
        contentLength = bodyBytes.length;
        writeHeader();
        writeBody(bodyBytes);

        if (path != null) {
            writeFile();
        }
    }

    protected void writeFile() throws IOException {
        Files.write(path, data);
    }

    protected void writeHeader() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(HEADER_SIZE);
        byteBuffer.putInt(version);
        byteBuffer.putInt(compressionAlgorithm);
        byteBuffer.putInt(contentType);
        byteBuffer.putInt(contentLength);
        data = byteBuffer.array();
    }

    protected byte[] getBodyBytesForWrite() throws IOException {
        return body;
    }

    protected void writeBody(byte[] body) {
        System.arraycopy(body, 0, data, HEADER_SIZE, body.length);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getCompressionAlgorithm() {
        return compressionAlgorithm;
    }

    public void setCompressionAlgorithm(int compressionAlgorithm) {
        this.compressionAlgorithm = compressionAlgorithm;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
