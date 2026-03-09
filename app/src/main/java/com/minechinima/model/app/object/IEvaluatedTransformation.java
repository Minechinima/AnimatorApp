package com.minechinima.model.app.object;

import org.joml.Matrix4d;

public interface IEvaluatedTransformation extends ITransformationComponent {
    Matrix4d world();
}
