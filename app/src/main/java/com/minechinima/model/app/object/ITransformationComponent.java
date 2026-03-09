package com.minechinima.model.app.object;

import com.minechinima.model.api.IDataPointer;
import com.minechinima.model.api.IDataTree;
import com.minechinima.model.api.values.vectors.IVector3f;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.values.vectors.Vector3fValue;

//TODO proof of concept that depsgraph components can be modularized and reusable across different data types.
public interface ITransformationComponent extends IDataTree {
    public IVector3f translation();
    public IVector3f rotation();
    public IVector3f scale();
    public IDataPointer<? extends ITransformationComponent> parent();
}
