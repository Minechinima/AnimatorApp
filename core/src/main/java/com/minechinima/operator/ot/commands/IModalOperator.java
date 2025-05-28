package com.minechinima.operator.ot.commands;

import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorContext;
import com.minechinima.utils.Pair;

import javax.annotation.Nullable;
import java.util.Queue;

/**
 * For Operators that support modal mode.
 * @param <C>
 */
public interface IModalOperator<C extends IContext> extends IOperator<C> {
    /**
     * Interactive mode of the operator.
     * @param context
     * @return <li>
     *     <ul>
     *         {@link OpModalFlag#MODAL_RUNNING} to continue the operator and call this method for the next event.
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
    Pair<Modal, Queue<IControllerCommand>> modal(OperatorContext<C> context, KeyMapEvent event) throws UserException;

    /**
     * Called when the operator is confirmed by an external system. The FINISHED flag does not trigger this.
     * @param context
     * @param event
     * @return
     * @throws UserException
     */
    default Pair<OpExecuteFlag, Queue<IControllerCommand>> confirmModal(OperatorContext<C> context, @Nullable KeyMapEvent event) throws UserException {
        return new Pair<>(OpExecuteFlag.FINISHED, null);
    }

    //TODO make an abstraction layer so you don't return UIElement for info but a set of status values
    // like progressbar 0-1, text, color etc., which in turn return / render the appropiate UIElement.
    // NO UI layer coupling in operator framework so UI can be independently developed
    /**
     *
     * @param info optional information string for the header
     *
     * @param flag
     */
    public record Modal(@Nullable String info, OpModalFlag flag) {
        public Modal(OpModalFlag flag) {
            this(null, flag);
        }
    }
}
