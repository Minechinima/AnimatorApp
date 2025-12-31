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
}
