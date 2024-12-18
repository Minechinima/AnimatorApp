package com.minechinima.operator;

/**
 * This Operator can only interact with the UI. It cannot perform changes to the app database, at least not directly.
 * <br><br>
 * A UIOperator can still perform data changes, but only by interacting with the UI e.g.
 * clicking on a button.
 * <br><br>
 * The distinction between a UIOperator and a normal Operator is necessary to allow running a UIOperator while a
 * a normal Operator is blocking the execution of other Operators. If this happens with an Operator running in UI mode,
 * we still need to be able to interact with the UI of the Operator.
 * @implNote Prevent access to any other UI structure that can provide access to database.
 */
public class UIOperator<T extends OperatorProperties> extends AbstractOperator<T, UIOperatorContext> {
    protected UIOperator(T props) {
        super(props);
    }
}
