package com.minechinima.controller;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import org.jetbrains.annotations.Contract;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class IControllerCommand implements Iterable<IControllerCommand> {
    /**
     * @throws UserException
     * @implNote protected to prevent anything other than {@link ControllerManager} from executing controller commands.
     * Enforce architecture design.
     */
    @Contract(pure = false)
    protected abstract void execute() throws UserException;

    /**
     * @return immutable list of the affected nodes by this controller in the order of operation.
     */
    @Contract(pure = true)
    public abstract List<BaseStructure> getAffectedNodes();

    /**
     * Utility method to directly build a chain of controller commands from any command.
     * @param command
     * @return the next node to continue building the linked list.
     */
    public ControllerChain next(IControllerCommand command) {
        return new ControllerChain(this).next(command);
    }

    /**
     * Utility method so a single IControllerCommmand can be passed to ControllerManager
     * @return
     */
    @Override
    public Iterator<IControllerCommand> iterator() {
        return new Iterator<>() {
            private IControllerCommand current = IControllerCommand.this;

            @Override
            public boolean hasNext() {
                return this.current != null;
            }

            @Override
            public IControllerCommand next() {
                if (this.current == null) {
                    throw new NoSuchElementException();
                }
                IControllerCommand cmd = this.current;
                this.current = null;
                return cmd;
            }
        };
    }
}
