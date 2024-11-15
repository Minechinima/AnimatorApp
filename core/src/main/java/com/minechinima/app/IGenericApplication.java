package com.minechinima.app;

import com.minechinima.controller.HistoryManager;
import com.minechinima.model.base.BaseValueHierarchy;
import org.jetbrains.annotations.Nullable;

public interface IGenericApplication<T extends BaseValueHierarchy<?>> {
    T getNameSpace();
    @Nullable
    HistoryManager getHistoryManager();
}
