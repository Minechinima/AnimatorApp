package com.minechinima.model.app.object;

import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.vectors.Vector3fValue;

import java.util.UUID;

public class BaseObject extends ValueGroup {
    public final Vector3fValue translation = new Vector3fValue("translation");
    public final Vector3fValue rotation = new Vector3fValue("rotation");
    public final Vector3fValue scale = new Vector3fValue("scale");
    public final BooleanValue viewportVisibility = new BooleanValue("visible_viewport", true);
    public final BooleanValue renderVisibility = new BooleanValue("visible_render", true);
    public final ObjectParentPointer parent;
    /**
     * The special data that defines this object like model, camera, entity, snowstorm etc.
     */
    public final ObjectData data = new ObjectData("data");

    public BaseObject(String id, BBDatabase database) {
        super(id);
        this.parent = new ObjectParentPointer("parent", this, database);
        this.init();
    }

    public BaseObject(String id, UUID uuid, BBDatabase database) {
        super(id, uuid);
        this.parent = new ObjectParentPointer("parent", this, database);
        this.init();
    }

    private void init() {
        this.addChild("translation", this.translation);
        this.addChild("rotation", this.rotation);
        this.addChild("scale", this.scale);
        this.addChild("parent", this.parent);
        this.addChild("data", this.data);
        this.addChild("viewport_visibility", this.viewportVisibility);
        this.addChild("render_visibility", this.renderVisibility);
    }
}
