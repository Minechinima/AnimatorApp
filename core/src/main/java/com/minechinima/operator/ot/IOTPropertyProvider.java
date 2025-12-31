package com.minechinima.operator.ot;

import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.ot.commands.IPropertyOperator;

/**
 * An OperatorType that provides OperatorProperties and can be configured.
 * @param <P> type of the operator properties
 */
public interface IOTPropertyProvider<O extends IPropertyOperator<?, P>, P extends OperatorProperties> {
    O getOperator();

    /**
     * @param props
     * @return a new Operator instance with the given property instance assigned.
     */
    O getOperator(P props);

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
