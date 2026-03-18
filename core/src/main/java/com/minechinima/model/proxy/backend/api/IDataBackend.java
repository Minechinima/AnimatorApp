package com.minechinima.model.proxy.backend.api;

import com.minechinima.model.api.IData;

/**
 *
 * @param <T> type of the next databackend implementation, so the implementation knows about its methods.
 */
public interface IDataBackend<T extends IDataBackend<T>> extends IData {
    void next(T handler);
}
