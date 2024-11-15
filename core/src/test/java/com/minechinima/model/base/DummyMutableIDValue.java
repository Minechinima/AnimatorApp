package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.ByteArrayType;
import com.minechinima.data.types.MapType;
import com.minechinima.model.dataoperations.datainterfaces.IDMutable;
import com.minechinima.utils.UUIDUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@TestOnly
class DummyMutableIDValue extends BaseStructure implements IDMutable {
    public UUID uuidTest;

    DummyMutableIDValue(String id) {
        super(id);
    }

    DummyMutableIDValue(String id, UUID uuid) {
        super(id, uuid);
    }

    @Override
    public MapType toData() {
        Map<String, BaseType<?>> map = new HashMap<>();
        map.put("uuid", new ByteArrayType(UUIDUtils.convertUUIDToBytes(this.getUUID())));
        return new MapType(map);
    }

    @Override
    public void fromData(BaseType<?> data) {

    }

    @Override
    public void setID(String id) {
        this._setID(id);
    }

    @Override
    @NotNull
    public UUID getUUID() {
        if (this.uuidTest != null) return this.uuidTest;
        return this.uuid;
    }
}
