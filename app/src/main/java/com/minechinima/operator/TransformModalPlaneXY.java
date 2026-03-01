package com.minechinima.operator;

import com.minechinima.model.AxisConstraintEnumValueStruct.AxisConstraint;

/** Constrains transformation to the XY plane (excludes Z axis). Triggered by Shift+Z. */
public class TransformModalPlaneXY extends TranslateModalAxis {
    public TransformModalPlaneXY() {
        super(AxisConstraint.XY);
    }
}
