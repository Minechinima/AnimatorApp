package com.minechinima.data.types;

import com.minechinima.data.io.IDataWriter;
import com.minechinima.data.io.IDataReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapType extends BaseType<Map<String, BaseType<?>>> implements Iterable<Map.Entry<String, BaseType<?>>> {
    private Map<String, BaseType<?>> value;

    protected MapType() {
        super();
    }

    public MapType(Map<String, BaseType<?>> map) {
        this.value = new HashMap<>(map);
    }

    @Nullable
    public BaseType<?> get(String key) {
        return this.value.get(key);
    }

    @Nullable
    public ByteType getByteType(String key) {
        return this.value.get(key) instanceof ByteType byteType ? byteType : null;
    }

    @Nullable
    public DoubleType getDoubleType(String key) {
        return this.value.get(key) instanceof DoubleType type ? type : null;
    }

    @Nullable
    public FloatType getFloatType(String key) {
        return this.value.get(key) instanceof FloatType type ? type : null;
    }

    @Nullable
    public IntArrayType getIntArrayType(String key) {
        return this.value.get(key) instanceof IntArrayType type ? type : null;
    }

    @Nullable
    public ByteArrayType getByteArrayType(String key) {
        return this.value.get(key) instanceof ByteArrayType type ? type : null;
    }

    @Nullable
    public IntType getIntType(String key) {
        return this.value.get(key) instanceof IntType type ? type : null;
    }

    @Nullable
    public LongArrayType getLongArrayType(String key) {
        return this.value.get(key) instanceof LongArrayType type ? type : null;
    }

    @Nullable
    public ListType getListType(String key) {
        return this.value.get(key) instanceof ListType type ? type : null;
    }

    @Nullable
    public LongType getLongType(String key) {
        return this.value.get(key) instanceof LongType type ? type : null;
    }

    @Nullable
    public ShortType getShortType(String key) {
        return this.value.get(key) instanceof ShortType type ? type : null;
    }

    @Nullable
    public StringType getStringType(String key) {
        return this.value.get(key) instanceof StringType type ? type : null;
    }

    @Nullable
    public MapType getMapType(String key) {
        return this.value.get(key) instanceof MapType type ? type : null;
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeMap(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextMap();
    }

    @Override
    public Map<String, BaseType<?>> getValue() {
        return value;
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, BaseType<?>>> iterator() {
        return this.value.entrySet().iterator();
    }
}
