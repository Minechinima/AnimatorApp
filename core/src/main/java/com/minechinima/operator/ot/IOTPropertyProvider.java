package com.minechinima.operator.ot;

import com.minechinima.operator.OperatorPropertiesGroupStruct;

/**
 * An OperatorType that provides OperatorProperties and can be configured.
 * @param <P> type of the operator properties
 */
public interface IOTPropertyProvider<P extends OperatorPropertiesGroupStruct> {
    /**
     * @return a new instance of the OperatorProperties
     */
    P createProps();

    /**
     * Add the given properties into the data tree.
     * @param props
     */
    void addRuntimeProps(P props);

    /**
     * Remove the given properties from the data tree.
     * @param props
     */
    void removeRuntimeProps(P props);
}
