package com.minechinima.model.utils;

import com.minechinima.data.types.MapType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.utils.UUIDUtils;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.UUID;

public record ID(String id, @Nullable UUID uuid) {
    public ID(String id) {
        this(id, null);
    }

    public MapType toData() {
        MapType map = new MapType(new HashMap<>());
        map.getValue().put("id", new StringType(this.id));
        if (this.uuid != null) {
            return UUIDUtils.putUUID(map, "uuid", this.uuid);
        } else {
            return map;
        }
    }

    public static ID parse(MapType id) throws BaseTypeCannotBeParsed {
        if (id.getStringType("id") == null) {
            throw new BaseTypeCannotBeParsed("");
        }
        UUID uuid = UUIDUtils.readUUIDorNull(id, "uuid");
        return new ID(id.getStringType("id").getValue(), uuid);
    }
}
