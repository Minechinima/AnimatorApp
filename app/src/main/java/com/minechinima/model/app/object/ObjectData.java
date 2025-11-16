package com.minechinima.model.app.object;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.LongType;
import com.minechinima.data.types.MapType;
import com.minechinima.model.values.AbstractEnumValueHierarchyReg;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.utils.GenericRegistry;
import com.minechinima.utils.IGenericRegistry;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ObjectData extends AbstractEnumValueHierarchyReg<ObjectType> {
    public static final Map<Pair<Long, Class<? extends ObjectType>>, Supplier<ObjectType>> OBJECT_TYPE_FACTORY = new HashMap<>();

    public static final IGenericRegistry<ObjectType> OBJECT_TYPE_REGISTRY = new GenericRegistry<>();

    static {
        OBJECT_TYPE_REGISTRY.register(0, EntityType.class, EntityType::new);
        OBJECT_TYPE_REGISTRY.register(1, CameraType.class, CameraType::new);
    }

    public ObjectData(String id) {
        super(id, OBJECT_TYPE_REGISTRY);
    }

    @Override
    public BaseType<?> valueToData() {
        MapType map = new MapType(new HashMap<>());
        if (this.get() == null) return map;

        for (Pair<Long, Class<? extends ObjectType>> registered : OBJECT_TYPE_FACTORY.keySet()) {
            if (registered.getB().equals(this.get().getClass())) {
                map.getValue().put("type_id", new LongType(registered.getA()));
                map.getValue().put("data", this.get().toData());
                break;
            }
        }

        return map;
    }

    @Override
    public @Nullable ObjectType valueFromData(BaseType<?> value) throws BaseTypeCannotBeParsed {
        if (!value.isMap()) {
            throw new BaseTypeCannotBeParsed("");
        }

        MapType data = value.asMapType();
        this.set(null);
        LongType id;
        ObjectType type = null;
        if ((id = data.getLongType("type_id")) != null) {
            for (Pair<Long, Class<? extends ObjectType>> registered : OBJECT_TYPE_FACTORY.keySet()) {
                if (registered.getA() == id.getValue()) {
                    type = OBJECT_TYPE_FACTORY.get(registered).get();
                    break;
                }
            }
        }

        if (type == null) {
            throw new BaseTypeCannotBeParsed("");
        }
        if (data.get("data") != null) type.fromData(data.get("data"));
        return type;
    }
}
