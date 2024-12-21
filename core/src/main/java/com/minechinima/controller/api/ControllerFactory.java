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
        if (this.owner instanceof IDMutable) {
            return Optional.of(new ControllerSetID(this.owner, newID));
        }
        return Optional.empty();
    }

    public class ControllerSetID implements IControllerCommand {
        private final BaseStructure owner;
        private final String val;

        public ControllerSetID(BaseStructure owner, String val) {
            this.owner = owner;
            this.val = val;
        }

        @Override
        public void execute() throws UserException {
            if (this.owner instanceof IDMutable mutable) {
                mutable.setID(this.val);
            }
        }

        @Override
        public List<BaseStructure> getAffectedNodes() {
            return this.owner instanceof IDMutable ? List.of(this.owner) : List.of();
        }
    }
}
