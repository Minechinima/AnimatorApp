package com.minechinima.model.app.object;

import org.joml.Matrix4d;

public interface IEvaluatedConstraintsComponent extends IConstraintsComponent {
    /**
     * @return Constraint matrix in object space, after parenting. This can be applied
     * on top of the world transformation.
     */
    Matrix4d constraintLocal();
}
