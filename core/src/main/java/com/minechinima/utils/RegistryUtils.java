package com.minechinima.utils;

import com.minechinima.data.IDataSerializable;
import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.ByteType;
import com.minechinima.data.types.IntType;
import com.minechinima.data.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RegistryUtils {
    /**
     * Serialize the value by using {@link IDataSerializable#toData()} and store the registry information for deserialization.
     * @param registry
     * @param value
     * @return
     * @param <T>
     */
    @Nullable
    public static <T extends IDataSerializable<?>> MapType serializeObjectData(IGenericRegistry<T> registry, T value) {
        Integer id = registry.getID(value);
        if (id == null) return null;
        Map<String, BaseType<?>> data = new HashMap<>();
        data.put("id", new IntType(id));
        data.put("data", value.toData());
        return new MapType(data);
    }

    /**
     * Instantiate the object using the registry information and deserialize custom data using {@link IDataSerializable#fromData(BaseType)}.
     * @param registry
     * @param baseType
     * @return
     * @param <T>
     */
    @Nullable
    public static <T extends IDataSerializable<BaseType<?>>> T deserializeObjectData(IGenericRegistry<T> registry, BaseType<?> baseType) {
        MapType data;
        if ((data = baseType.asMapType()) == null) return null;
        IntType idData;
        if (data.get("id") == null || (idData = data.get("id").asIntType()) == null) return null;
        T value = registry.getNew(idData.getValue());

        //TODO generic of IDataSerializable prevents this method deserializing anything as we do not know what BaseType type the fromData argument is
        if (data.get("data") != null) {
            value.fromData(data.get("data"));
        }

        return value;
    }

    /**
     * Instantiate the object using the registry information and deserialize custom data using {@link IDataSerializable#fromData(BaseType)}.
     * @param registry
     * @param baseType
     * @return
     * @param <T>
     */
    @NotNull
    public static <T extends IDataSerializable<BaseType<?>>> T deserializeObjectData(IGenericDefaultRegistry<T> registry, BaseType<?> baseType) {
        final MapType data;
        final IntType idData;

        if ((data = baseType.asMapType()) == null ||
                data.get("id") == null || (idData = data.get("id").asIntType()) == null) {
            return registry.getNewDefault();
        }

        T value = registry.getNew(idData.getValue());
        if (value == null) return registry.getNewDefault();

        //TODO generic of IDataSerializable prevents this method deserializing anything as we do not know what BaseType type the fromData argument is
        if (data.get("data") != null) {
            value.fromData(data.get("data"));
        }

        return value;
    }

    /**
     * Only serialize the registry information of the value. No custom object data is stored.
     * @param registry
     * @param value
     * @return
     * @param <T>
     */
    @Nullable
    public static <T> MapType serialize(IGenericRegistry<T> registry, T value) {
        //TODO think about null here
        Integer id = registry.getID(value);
        if (id == null) return null;
        Map<String, BaseType<?>> data = new HashMap<>();
        data.put("id", new IntType(id));
        return new MapType(data);
    }

    /**
     * Only instantiate the object by using the registry information. Does not deserialize custom data of the object.
     * @param basetype
     * @param registry
     * @return
     * @param <T>
     */
    @Nullable
    public static <T> T deserialize(BaseType<?> basetype, IGenericRegistry<T> registry) {
        MapType data;
        if ((data = basetype.asMapType()) == null) return null;
        IntType idData;
        if (data.get("id") == null || (idData = data.get("id").asIntType()) == null) return null;

        return registry.getNew(idData.getValue());
    }

    /**
     * Only instantiate the object by using the registry information. Does not deserialize custom data of the object.
     * @param basetype
     * @param registry
     * @return
     * @param <T>
     */
    @NotNull
    public static <T> T deserialize(BaseType<?> basetype, IGenericDefaultRegistry<T> registry) {
        MapType data;
        if ((data = basetype.asMapType()) == null) return registry.getNewDefault();
        IntType idData;
        if (data.get("id") == null || (idData = data.get("id").asIntType()) == null) return registry.getNewDefault();

        T value = registry.getNew(idData.getValue());

        return value == null ? registry.getNewDefault() : value;
    }
}
