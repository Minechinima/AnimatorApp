package com.minechinima.model.proxy.backend.api;

/**
 * Currently mostly for structuring reasons and for easier generic handling.
 * <br><br>
 * This is used for backends for data classes, which don't have any special getter or setter methods, as they
 * are a collection of assigned attributes.
 */
public interface IDataClassBackend extends IDataTreeBackend<IDataClassBackend> {
}
