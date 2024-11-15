package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class UUIDType extends BaseType<UUID> {
    private UUID value;

    protected UUIDType() {
        super();
    }

    public UUIDType(@NotNull UUID value) {
        this.value = value;
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeUUID(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextUUID();
    }

    @Override
    public UUID getValue() {
        return value;
    }
}
