package com.minechinima.controller;

import com.minechinima.controller.factories.ControllerFactory;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.app.object.ObjectDataManager;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;

import java.util.List;

public class ObjectDataManagerController extends ControllerFactory<ObjectDataManager> {
    public ObjectDataManagerController(ObjectDataManager owner) {
        super(owner);
    }

    public IControllerCommand deleteObject(BaseObject obj) {
        return new IControllerCommand() {
            @Override
            public void execute() throws UserException {
                ObjectDataManagerController.this.owner.remove(obj);
            }

            @Override
            public List<BaseStructure> getAffectedNodes() {
                return List.of(obj);
            }
        };
    }
}
