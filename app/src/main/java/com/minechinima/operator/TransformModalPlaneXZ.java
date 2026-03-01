package com.minechinima.operator;

import com.minechinima.model.AxisConstraintEnumValueStruct.AxisConstraint;

/** Constrains transformation to the XZ plane (excludes Y axis). Triggered by Shift+Y. */
public class TransformModalPlaneXZ extends TranslateModalAxis {
    public TransformModalPlaneXZ() {
        super(AxisConstraint.XZ);
    }
}
