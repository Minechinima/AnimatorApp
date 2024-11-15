package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class StringType extends BaseType<String> {
    private String value;

    protected StringType() {
        super();
    }

    public StringType(@NotNull String value) {
        this.value = value;
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeString(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextString();
    }

    @Override
    public String getValue() {
        return value;
    }
}
