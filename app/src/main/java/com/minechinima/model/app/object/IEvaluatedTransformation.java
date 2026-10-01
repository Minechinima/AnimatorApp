package com.minechinima.model.app.object;

import org.joml.Matrix4d;

public interface IEvaluatedTransformation extends ITransformationComponent {
    Matrix4d basis();
    /**
     * @return Parent relative transformation matrix.
     */
    Matrix4d local();

    /**
     * @return World transformation matrix, including parent transformations.
     */
    Matrix4d parentWorld();
}
