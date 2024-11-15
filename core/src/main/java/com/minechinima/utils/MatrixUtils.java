package com.minechinima.utils;

import org.joml.Matrix3f;
import org.joml.Matrix4d;

public class MatrixUtils {
    /**
     *
     * @param matrix3f
     * @return an identity matrix where the upper left 3x3 part has been replaced with the provided 3x3 matrix.
     */
    public static Matrix4d toMatrix4d(Matrix3f matrix3f) {
        return new Matrix4d(matrix3f.m00, matrix3f.m01, matrix3f.m02, 0,
                            matrix3f.m10, matrix3f.m11, matrix3f.m12, 0,
                            matrix3f.m20, matrix3f.m21, matrix3f.m22, 0,
                       0, 0, 0, 1);
    }
}
