package com.minechinima.controller;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import org.jetbrains.annotations.Contract;

import java.util.List;

public abstract class IControllerCommand {
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
}
