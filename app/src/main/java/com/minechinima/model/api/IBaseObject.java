package com.minechinima.model.api;

import com.minechinima.model.api.values.IDataValue;
import com.minechinima.model.api.values.vectors.IVector3f;
import com.minechinima.model.app.object.ITransformationComponent;
import com.minechinima.model.logging.datainterfaces.IDMutable;

public interface IBaseObject extends IDataTree, IDMutable, ITransformationComponent {
    IVector3f translation();
    IVector3f rotation();
    IVector3f scale();
    IDataValue<Boolean> viewportVisibility();
    IDataValue<Boolean> renderVisibility();

    /**
     *
     * @return
     * @implNote Note how the type of the IDataPointer is still IBaseObject, so the UI does not need to know
     * about BaseStructure.
     */
    IDataPointer<? extends IBaseObject> parent();
}
