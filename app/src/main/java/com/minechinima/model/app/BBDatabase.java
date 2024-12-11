package com.minechinima.model.app;

import com.minechinima.model.app.object.ObjectDataManager;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.model.scene.SceneDataManager;

public class BBDatabase extends ValueGroup {
    public final ObjectDataManager objects = new ObjectDataManager("objects", this);
    public final SceneDataManager scenes = new SceneDataManager("scenes", this);

    public BBDatabase(String id) {
        super(id);
        this.addChild("objects", this.objects);
    }
}
