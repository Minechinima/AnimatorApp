package com.minechinima.operator.ot.commands;

import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.CursorInfo;
import com.minechinima.utils.Pair;

import javax.annotation.Nullable;
import java.util.Queue;

/**
 * For Operators that support modal mode.
 * @param <C>
 */
public interface IModalOperator<C> extends IOperator<C> {
    /**
     * Interactive mode of the operator.
     * @param context
     * @param cursorInfo
     * @return <li>
     *     <ul>
     *         {@link OpModalFlag#MODAL_RUNNING} to continue the operator and call this method in the next update cycle.
     *     </ul>
     *     <ul>
     *         {@link OpModalFlag#FINISHED} operator finished its job successfully and should end.
     *     </ul>
     *     <ul>
     *         {@link OpModalFlag#CANCELLED} operator canceled the job or ended prematurely and should end.
     *      </ul>
     * </li>
     *
     * @throws UserException
     */
    default Pair<Modal, Queue<IControllerCommand>> modal(C context, CursorInfo cursorInfo) throws UserException {
        /*
         * throw exception by default to debug incorrect handling / implementation of the states
         * e.g. invoke() returning MODAL without modal implementation
         */
        throw new UnsupportedOperationException("Method modal() not implemented.");
    }

    /**
     * Called when the Operator modal mode is canceled from external systems, e.g. right click, during modal mode.
     * <br><br>
     * This can be used to undo operations that are not part of the database.
     * @param context
     */
    default void cancelModal(C context) {
    }

    /**
     *
     * @param info optional information string for the header
     *             TODO make an abstraction layer so you don't return UIElement here but a set of status values
     *             like progressbar 0-1, text, color etc., which in turn return / render the appropiate UIElement.
     *             NO UI layer coupling in operator framework so UI can be independently developed
     * @param flag
     */
    public record Modal(@Nullable String info, OpModalFlag flag) {
        public Modal(OpModalFlag flag) {
            this(null, flag);
        }
    }
}
