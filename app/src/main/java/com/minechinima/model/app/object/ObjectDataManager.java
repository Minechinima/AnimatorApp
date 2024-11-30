package com.minechinima.model.app.object;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.base.BaseValueMap;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ObjectDataManager extends BaseValueMap<BaseObject> {
    private final BBDatabase database;

    public ObjectDataManager(String id, BBDatabase database) {
        super(id);
        this.database = database;
    }

    @Override
    protected @NotNull BaseObject instantiate(String id, UUID uuid, BaseType<?> data) {
        BaseObject obj = uuid == null ? new BaseObject(id, this.database) : new BaseObject(id, uuid, this.database);
        obj.fromData(data);
        return obj;
    }
}
