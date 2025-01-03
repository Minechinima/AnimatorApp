package com.minechinima.controller.api;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.base.BaseValue;

import java.util.List;

public class ValueControllerFactory<T> extends ControllerFactory<BaseValue<T>> {
    public ValueControllerFactory(BaseValue<T> owner) {
        super(owner);
    }

    public IControllerCommand set(T value) {
        return new ControllerSetValue(this.owner, value);
    }

    private class ControllerSetValue implements IControllerCommand {
        private final T value;
        private final BaseValue<T> owner;

        private ControllerSetValue(BaseValue<T> owner, T value) {
            this.value = value;
            this.owner = owner;
        }

        @Override
        public void execute() throws UserException {
            if (!this.owner.canSet(this.value)) {
                throw new UserException("Cannot set the value of " + this.owner.getIdentifier());
            }

            this.owner.set(this.value);
        }

        @Override
        public List<BaseStructure> getAffectedNodes() {
            return List.of(ValueControllerFactory.this.owner);
        }
    }
}
