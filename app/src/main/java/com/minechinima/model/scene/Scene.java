package com.minechinima.model.scene;

import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.model.logging.datainterfaces.IDMutable;
import com.minechinima.model.values.lists.ValuePointerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Scene extends ValueGroup implements IDMutable {
    /**
     * Direct collection children
     */
    public final ValuePointerList<BaseObject> rootCollection;

    public Scene(String id, BBDatabase database) {
        super(id);
        this.rootCollection = new ValuePointerList<>("objects", BaseObject.class, database);
        this.addChild("objects", this.rootCollection);
    }

    public Scene(String id, UUID uuid, BBDatabase database) {
        super(id, uuid);
        this.rootCollection = new ValuePointerList<>("objects", BaseObject.class, database);
        this.addChild("objects", this.rootCollection);
    }

    @Override
    public void setID(String identifier) {
        this._setID(identifier);
    }

    @Override
    public @NotNull UUID getUUID() {
        return this.uuid;
    }
}
