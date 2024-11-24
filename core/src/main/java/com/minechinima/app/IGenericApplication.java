package com.minechinima.app;

import com.minechinima.controller.Controller;
import com.minechinima.controller.HistoryManager;
import com.minechinima.model.DataTreeManager;

public interface IGenericApplication<T extends DataTreeManager<?>> {
    T getDataTree();
    Controller getController();
}
