package com.minechinima.data.types;

import com.minechinima.data.io.IDataWriter;
import com.minechinima.data.io.IDataReader;

import java.io.IOException;
import java.util.Arrays;

public class ByteArrayType extends BaseType<byte[]> {
    private byte[] value = new byte[0];

    protected ByteArrayType() {
        super();
    }

    public ByteArrayType(byte[] value) {
        this.value = Arrays.copyOf(value, value.length);
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeByteArray(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextByteArray();
    }

    @Override
    public byte[] getValue() {
        return value;
    }
}
