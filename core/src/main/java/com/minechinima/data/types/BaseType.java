package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class BaseType<T> {
    // Based on Minecraft NBT tag type ids
    public static final byte BYTE = 1;
    public static final byte SHORT = 2;
    public static final byte INT = 3;
    public static final byte LONG = 4;
    public static final byte FLOAT = 5;
    private static final byte DOUBLE = 6;
    public static final byte BYTE_ARRAY = 7;
    public static final byte STRING = 8;
    public static final byte LIST = 9;
    public static final byte MAP = 10; // Minecraft: Compound
    public static final byte INT_ARRAY = 11;
    public static final byte LONG_ARRAY = 12;
    public static final byte UUID = 13;

    public abstract T getValue();

    public boolean isByte() {
        return this instanceof ByteType;
    }

    @Nullable
    public ByteType asByteType() {
        return this instanceof ByteType type ? type : null;
    }

    public boolean isShort() {
        return this instanceof ShortType;
    }

    @Nullable
    public ShortType asShortType() {
        return this instanceof ShortType type ? type : null;
    }

    public boolean isInt() {
        return this instanceof IntType;
    }

    @Nullable
    public IntType asIntType() {
        return this instanceof IntType type ? type : null;
    }

    public boolean isLong() {
        return this instanceof LongType;
    }

    @Nullable
    public LongType asLongType() {
        return this instanceof LongType type ? type : null;
    }

    public boolean isFloat() {
        return this instanceof FloatType;
    }

    @Nullable
    public FloatType asFloatType() {
        return this instanceof FloatType type ? type : null;
    }

    public boolean isDouble() {
        return this instanceof DoubleType;
    }

    @Nullable
    public DoubleType asDoubleType() {
        return this instanceof DoubleType type ? type : null;
    }

    public boolean isByteArray() {
        return this instanceof ByteArrayType;
    }

    @Nullable
    public ByteArrayType asByteArrayType() {
        return this instanceof ByteArrayType type ? type : null;
    }

    public boolean isString() {
        return this instanceof StringType;
    }

    @Nullable
    public StringType asStringType() {
        return this instanceof StringType type ? type : null;
    }

    public boolean isList() {
        return this instanceof ListType;
    }

    @Nullable
    public ListType asListType() {
        return this instanceof ListType type ? type : null;
    }

    public boolean isMap() {
        return this instanceof MapType;
    }

    @Nullable
    public MapType asMapType() {
        return this instanceof MapType type ? type : null;
    }

    public boolean isIntArray() {
        return this instanceof IntArrayType;
    }

    @Nullable
    public IntArrayType asIntArrayType() {
        return this instanceof IntArrayType type ? type : null;
    }

    public boolean isLongArray() {
        return this instanceof LongArrayType;
    }

    @Nullable
    public LongArrayType asLongArrayType() {
        return this instanceof LongArrayType type ? type : null;
    }

    public boolean isNumber() {
        return this instanceof NumberType;
    }

    @Nullable
    public NumberType<?> asNumberType() {
        return this instanceof NumberType<?> type ? type : null;
    }

    public boolean isArray() {
        return isByteArray() || isIntArray() || isLongArray();
    }

    public boolean isUUID() {
        return this instanceof UUIDType;
    }

    public UUIDType asUUIDType() {
        return this instanceof UUIDType type ? type : null;
    }

    public static BaseType<?> readFromData(IDataReader reader) throws IOException {
        if (!reader.hasNext()) {
            throw new IllegalArgumentException("Type not set");
        }

        byte type = reader.readNextType();

        if (!reader.hasNext()) {
            throw new IllegalArgumentException("No data to read");
        }

        BaseType<?> baseType = switch (type) {
            case BYTE -> new ByteType();
            case SHORT -> new ShortType();
            case INT -> new IntType();
            case LONG -> new LongType();
            case FLOAT -> new FloatType();
            case DOUBLE -> new DoubleType();
            case BYTE_ARRAY -> new ByteArrayType();
            case STRING -> new StringType();
            case LIST -> new ListType();
            case MAP -> new MapType();
            case INT_ARRAY -> new IntArrayType();
            case LONG_ARRAY -> new LongArrayType();
            case UUID -> new UUIDType();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        baseType.fromData(reader);

        return baseType;
    }
    
    public static void writeToData(IDataWriter writer, BaseType<?> baseType) throws IOException {
        if (baseType instanceof ByteType) {
            writer.writeType(BYTE);
        } else if (baseType instanceof ShortType) {
            writer.writeType(SHORT);
        } else if (baseType instanceof IntType) {
            writer.writeType(INT);
        } else if (baseType instanceof LongType) {
            writer.writeType(LONG);
        } else if (baseType instanceof FloatType) {
            writer.writeType(FLOAT);
        } else if (baseType instanceof DoubleType) {
            writer.writeType(DOUBLE);
        } else if (baseType instanceof ByteArrayType) {
            writer.writeType(BYTE_ARRAY);
        } else if (baseType instanceof StringType) {
            writer.writeType(STRING);
        } else if (baseType instanceof ListType) {
            writer.writeType(LIST);
        } else if (baseType instanceof MapType) {
            writer.writeType(MAP);
        } else if (baseType instanceof IntArrayType) {
            writer.writeType(INT_ARRAY);
        } else if (baseType instanceof LongArrayType) {
            writer.writeType(LONG_ARRAY);
        } else if (baseType instanceof UUIDType) {
            writer.writeType(UUID);
        } else {
            throw new IllegalArgumentException("Unknown type");
        }

        baseType.toData(writer);
    }

    protected abstract void toData(IDataWriter writer) throws IOException;

    protected abstract void fromData(IDataReader reader) throws IOException;
}