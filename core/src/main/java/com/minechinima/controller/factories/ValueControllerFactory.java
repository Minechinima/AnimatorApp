package com.minechinima.controller.factories;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.datainterfaces.IValue;

import java.util.List;

/**
 * Generic Controller factory for {@link IValue} implementations
 * @param <J> datatype of the actual value.
 * @param <T> data node type
 */
public class ValueControllerFactory<J, T extends BaseStructure & IValue<J> & IControllerFactoryProvider> extends ControllerFactory<T> {
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
