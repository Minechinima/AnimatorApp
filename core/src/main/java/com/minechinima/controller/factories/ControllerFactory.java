package com.minechinima.controller.factories;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.datainterfaces.IDMutable;

import java.util.List;
import java.util.Optional;

public class ControllerFactory<T extends BaseStructure & IControllerFactoryProvider> {
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

    public IControllerCommand setFromData(BaseType<?> data) {
        return new IControllerCommand() {
            @Override
            protected void execute() throws UserException {
                ControllerFactory.this.owner.fromData(data);
            }

            @Override
            public List<BaseStructure> getAffectedNodes() {
                return List.of(ControllerFactory.this.owner);
            }
        };
    }
}
