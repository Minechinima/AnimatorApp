package com.minechinima.model.proxy.backend.api;

import com.minechinima.model.api.IData;
import com.minechinima.model.api.IDataTree;
import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.logging.DataOperationBus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @param <T> type of the next databackend implementation, so the implementation knows about its methods.
 */
public interface IDataBackend<T extends IDataBackend<T>> extends IData {
    void next(T handler);
}
