package com.minechinima.model.app.object;

import com.minechinima.model.app.BBDatabase;
import org.joml.Matrix4d;

public class BaseObjectRuntime extends BaseObject implements IEvaluatedTransformation {
    public Matrix4d worldMatrix;

    public BaseObjectRuntime(String id, BBDatabase database) {
        super(id, database);
    }

    @Override
    public Matrix4d world() {
        return this.worldMatrix;
    }
}
