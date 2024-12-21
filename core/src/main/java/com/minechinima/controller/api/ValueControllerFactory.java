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
        return new ControllerSetValue(value);
    }

    private class ControllerSetValue implements IControllerCommand {
        private final T value;

        private ControllerSetValue(T value) {
            this.value = value;
        }

        @Override
        public void execute() throws UserException {
            ValueControllerFactory.this.owner.set(this.value);
        }

        @Override
        public List<BaseStructure> getAffectedNodes() {
            return List.of(ValueControllerFactory.this.owner);
        }
    }
}
