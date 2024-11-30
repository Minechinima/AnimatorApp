package com.minechinima.model.app;

import com.minechinima.model.app.object.ObjectDataManager;
import com.minechinima.model.base.BaseImmutableValueHierarchy;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.ValueGroup;
import com.minechinima.model.scene.SceneDataManager;
import com.minechinima.model.utils.ValuePath;

public class BBDatabase extends ValueGroup {
    public final ObjectDataManager objects = new ObjectDataManager("objects", this);
    public final SceneDataManager scenes = new SceneDataManager("scenes", this);

    public BBDatabase(String id) {
        super(id);
        this.addChild("objects", this.objects);
    }
}
