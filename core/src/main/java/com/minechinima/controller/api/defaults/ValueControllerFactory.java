package com.minechinima.controller.api.defaults;

import com.minechinima.controller.api.ControllerFactory;
import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.values.base.BaseValue;

import java.util.List;

/**
 * Generic Controller factory for {@link IValue} implementations
 * @param <J>
 * @param <T>
 */
public class ValueControllerFactory<J, T extends BaseStructure & IValue<J>> extends ControllerFactory<T> {
    public ValueControllerFactory(T owner) {
        super(owner);
    }

    public IControllerCommand set(J value) {
        return new IControllerCommand() {
            @Override
            public void execute() throws UserException {
                if (!ValueControllerFactory.this.owner.canSet(value)) {
                    throw new UserException("Cannot set the value of " + ValueControllerFactory.this.owner.getIdentifier());
                }

                ValueControllerFactory.this.owner.set(value);
            }

            @Override
            public List<BaseStructure> getAffectedNodes() {
                return List.of(ValueControllerFactory.this.owner);
            }
        };
    }
}
