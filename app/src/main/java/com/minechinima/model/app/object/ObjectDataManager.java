package com.minechinima.model.app.object;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.base.utils.BaseOperationsUtils;
import com.minechinima.model.events.types.changes.ValueAddEvent;
import com.minechinima.model.values.base.BaseValueMap;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ObjectDataManager extends BaseValueMap<BaseObject> {
    private final BBDatabase database;

    public ObjectDataManager(String id, BBDatabase database) {
        super(id);
        this.database = database;
    }

    public boolean add(@NotNull BaseObject value) {
        if(!value.isInitialized()) value.initChildren();
        return super.add(value);
    }

    @Override
    protected @NotNull BaseObject instantiate(String id, UUID uuid, BaseType<?> data) {
        BaseObject obj = uuid == null ? new BaseObject(id, this.database) : new BaseObject(id, uuid, this.database);
        obj.initChildren();
        obj.fromData(data);
        return obj;
    }
}
