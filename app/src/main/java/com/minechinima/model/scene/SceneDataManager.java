package com.minechinima.model.scene;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.base.BaseValueMap;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SceneDataManager extends BaseValueMap<Scene> {
    private final BBDatabase database;

    public SceneDataManager(String id, BBDatabase database) {
        super(id);
        this.database = database;
    }

    @Override
    protected @NotNull Scene instantiate(String id, UUID uuid, BaseType<?> data) {
        Scene scene = uuid == null ? new Scene(id, this.database) : new Scene(id, uuid, this.database);
        scene.fromData(data);
        return scene;
    }
}
