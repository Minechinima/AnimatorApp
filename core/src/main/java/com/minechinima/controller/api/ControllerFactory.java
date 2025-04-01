package com.minechinima.controller.api;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.datainterfaces.IDMutable;

import java.util.List;
import java.util.Optional;

public class ControllerFactory<T extends BaseStructure> {
    protected final T owner;

    public ControllerFactory(T owner) {
        this.owner = owner;
    }

    public Optional<IControllerCommand> setID(String newID) {
        if (this.owner instanceof IDMutable idMutable) {
            return Optional.of(new IControllerCommand() {
                @Override
                public void execute() throws UserException {
                    idMutable.setID(newID);
                }

                @Override
                public List<BaseStructure> getAffectedNodes() {
                    return List.of(ControllerFactory.this.owner);
                }
            });
        }
        return Optional.empty();
    }
}
