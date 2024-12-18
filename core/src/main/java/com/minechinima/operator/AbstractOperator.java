package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.flags.OpInvokeFlag;
import com.minechinima.operator.flags.OpModalFlag;

import javax.annotation.Nullable;

/**
 * An Operator represents a unique action that the user can execute with an InputEvent (mouse, keyboard, clicking on a UI).
 * <br><br>
 * User interaction describes the process of causing a change in the App / UI state with an input event from the user.
 * <br><br>
 * Every user interaction logic is encapsulated into a singular Operator that can be reused and allows more central management.
 * Furthermore, it allows to decouple the UI from the user interaction logic, making the UI code more clean and follow
 * single responsibility principle -> UI only does rendering of UI.
 * <br><br>
 * Important characteristics of an Operator:
 * <ul>
 *     <li>The behavior of an Operator can only be influenced by the values of the OperatorProperties.
 *     This requires that it has no access to input device events. Giving access to input events could allow the behavior to be
 *     dependent on those events and change it. This would make it hard to understand what actually happens in the Operator.</li>
 *     <li>Modal mode is the interactive mode where continuous user interaction can be performed. It receives information
 *     about the position and movement of the mouse.</li>
 *     <li>An Operator can be represented as a state machine, see documentation.
 *     This is why the methods have different return flag types.</li>
 * </ul>
 * @param <T> type of the properties.
 * @param <C> type of the context. This allows an app to define more context information
 */
public abstract class AbstractOperator<T extends OperatorProperties, C> {
    protected final T props;

    protected AbstractOperator(T props) {
        this.props = props;
    }

    public ModalResult modal(C context, CursorInfo cursorInfo) throws UserException {
        /*
         * throw exception by default to debug incorrect handling / implementation of the states
         * e.g. invoke() returning MODAL without modal implementation
         */
        throw new UnsupportedOperationException("Method modal() not implemented.");

    }

    public OpInvokeFlag invoke(C context) throws UserException {
        throw new UnsupportedOperationException("Method invoke() not implemented.");
    }

    public record ModalResult(@Nullable String info, OpModalFlag flag) {
        public ModalResult(OpModalFlag flag) {
            this(null, flag);
        }
    }
}
