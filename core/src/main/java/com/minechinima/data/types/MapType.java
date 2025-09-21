package com.minechinima.data.types;

import com.minechinima.data.io.IDataWriter;
import com.minechinima.data.io.IDataReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

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
    public Byte getByteValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof ByteType type ? type.getValue() : null;
    }

    @Nullable
    public DoubleType getDoubleType(String key) {
        return this.value.get(key) instanceof DoubleType type ? type : null;
    }

    @Nullable
    public Double getDoubleValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof DoubleType type ? type.getValue() : null;
    }

    @Nullable
    public FloatType getFloatType(String key) {
        return this.value.get(key) instanceof FloatType type ? type : null;
    }

    @Nullable
    public Float getFloatValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof FloatType type ? type.getValue() : null;
    }

    @Nullable
    public IntArrayType getIntArrayType(String key) {
        return this.value.get(key) instanceof IntArrayType type ? type : null;
    }

    @Nullable
    public int[] getIntArrayValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof IntArrayType type ? type.getValue() : null;
    }

    @Nullable
    public ByteArrayType getByteArrayType(String key) {
        return this.value.get(key) instanceof ByteArrayType type ? type : null;
    }

    @Nullable
    public byte[] getByteArrayValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof ByteArrayType type ? type.getValue() : null;
    }

    @Nullable
    public IntType getIntType(String key) {
        return this.value.get(key) instanceof IntType type ? type : null;
    }

    @Nullable
    public Integer getIntValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof IntType type ? type.getValue() : null;
    }

    @Nullable
    public LongArrayType getLongArrayType(String key) {
        return this.value.get(key) instanceof LongArrayType type ? type : null;
    }

    @Nullable
    public long[] getLongArrayValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof LongArrayType type ? type.getValue() : null;
    }

    @Nullable
    public ListType getListType(String key) {
        return this.value.get(key) instanceof ListType type ? type : null;
    }

    @Nullable
    public List<?> getListValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof ListType type ? type.getValue() : null;
    }

    @Nullable
    public LongType getLongType(String key) {
        return this.value.get(key) instanceof LongType type ? type : null;
    }

    @Nullable
    public Long getLongValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof LongType type ? type.getValue() : null;
    }

    @Nullable
    public ShortType getShortType(String key) {
        return this.value.get(key) instanceof ShortType type ? type : null;
    }

    @Nullable
    public Short getShortValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof ShortType type ? type.getValue() : null;
    }

    @Nullable
    public StringType getStringType(String key) {
        return this.value.get(key) instanceof StringType type ? type : null;
    }

    @Nullable
    public String getStringValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof StringType type ? type.getValue() : null;
    }

    @Nullable
    public NullType getNullType(String key) {
        return this.value.get(key) instanceof NullType type ? type : null;
    }

    @Nullable
    public UUIDType getUUIDType(String key) {
        return this.value.get(key) instanceof UUIDType type ? type : null;
    }

    @Nullable
    public UUID getUUIDValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof UUIDType type ? type.getValue() : null;
    }

    @Nullable
    public MapType getMapType(String key) {
        return this.value.get(key) instanceof MapType type ? type : null;
    }

    @Nullable
    public Map<String, BaseType<?>> getMapValue(String key) {
        BaseType v = this.value.get(key);
        return v instanceof MapType type ? type.getValue() : null;
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
