package com.minechinima.controller.factories;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.vectors.Vector3fValue;
import org.joml.Vector3f;

import java.util.List;

public class VectorControllerFactory extends ControllerFactory<Vector3fValue> {
    public VectorControllerFactory(Vector3fValue owner) {
        super(owner);
    }

    public IControllerCommand add(Vector3f add) {
        return new IControllerCommand() {
            private final Vector3fValue owner = VectorControllerFactory.this.owner;
            private final Vector3f offset = new Vector3f(add);

            @Override
            public void execute() throws UserException {
                this.owner.x.set(this.owner.x.get() + offset.x);
                this.owner.y.set(this.owner.y.get() + offset.y);
                this.owner.z.set(this.owner.z.get() + offset.z);
            }

            @Override
            public List<BaseStructure> getAffectedNodes() {
                return List.of(this.owner);
            }
        };
    }

    public IControllerCommand set(Vector3f val) {
        return new IControllerCommand() {
            private final Vector3fValue owner = VectorControllerFactory.this.owner;
            private final Vector3f set = new Vector3f(val);

            @Override
            public void execute() throws UserException {
                this.owner.x.set(this.set.x);
                this.owner.y.set(this.set.y);
                this.owner.z.set(this.set.z);
            }

            @Override
            public List<BaseStructure> getAffectedNodes() {
                return List.of(this.owner);
            }
        };
    }
}
