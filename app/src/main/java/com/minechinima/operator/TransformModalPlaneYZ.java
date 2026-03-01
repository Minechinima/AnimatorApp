package com.minechinima.operator;

import com.minechinima.model.AxisConstraintEnumValueStruct.AxisConstraint;

/** Constrains transformation to the YZ plane (excludes X axis). Triggered by Shift+X. */
public class TransformModalPlaneYZ extends TranslateModalAxis {
    public TransformModalPlaneYZ() {
        super(AxisConstraint.YZ);
    }
}
