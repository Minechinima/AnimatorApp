package com.minechinima.app;

import com.minechinima.controller.Controller;
import com.minechinima.controller.HistoryManager;
import com.minechinima.model.base.BaseHierarchyRoot;

public interface IGenericApplication<T extends BaseHierarchyRoot> {
    T getDataRoot();
    HistoryManager getHistoryManager();
    Controller getController();
}
