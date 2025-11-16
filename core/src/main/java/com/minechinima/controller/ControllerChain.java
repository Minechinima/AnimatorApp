package com.minechinima.controller;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows to reuse other controllers in a composition of controller commands.
 */
public class ControllerChain extends IControllerCommand {
    @NotNull
    private final IControllerCommand command;
    @Nullable
    private ControllerChain next;

    public ControllerChain(@NotNull IControllerCommand command) {
        this.command = command;
    }

    @Override
    public void execute() throws UserException {
        this.command.execute();
        if (this.next != null) this.next.execute();
    }

    @Override
    public List<BaseStructure> getAffectedNodes() {
        List<BaseStructure> affected = new ArrayList<>();
        ControllerChain next = this;

        while (next != null) {
            affected.addAll(next.command.getAffectedNodes());
            next = next.next;
        }

        return List.copyOf(affected);
    }
}
