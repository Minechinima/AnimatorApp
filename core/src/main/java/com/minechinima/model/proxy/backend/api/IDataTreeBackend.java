package com.minechinima.model.proxy.backend.api;

import com.minechinima.model.api.IData;
import com.minechinima.model.api.IDataTree;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * General backend for all classes that implement {@link IDataTree}.
 * Use this for maps, lists, arrays, data classes (attribute groups), everything that has child nodes.
 * @param <T>
 */
public interface IDataTreeBackend<T extends IDataTreeBackend<T>> extends IDataBackend<T>, IDataTree {
}
