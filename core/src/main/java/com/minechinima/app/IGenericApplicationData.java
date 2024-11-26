package com.minechinima.app;

import com.minechinima.controller.Controller;
import com.minechinima.model.DataTreeManager;

public interface IGenericApplicationData<T extends DataTreeManager<?>> {
    T getDataTree();
    Controller getController();
}
