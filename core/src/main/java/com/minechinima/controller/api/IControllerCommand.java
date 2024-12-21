package com.minechinima.controller.api;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import org.jetbrains.annotations.Contract;

import java.util.List;

public interface IControllerCommand {
    @Contract(pure = false)
    void execute() throws UserException;

    /**
     * @return immutable list of the affected nodes by this controller in the order of operation.
     */
    @Contract(pure = true)
    List<BaseStructure> getAffectedNodes();
}
