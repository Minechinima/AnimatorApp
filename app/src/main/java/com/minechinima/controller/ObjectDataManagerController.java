package com.minechinima.controller;

import com.minechinima.controller.api.ControllerFactory;
import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.app.object.ObjectDataManager;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.OpExecuteFlag;
import com.minechinima.utils.Pair;

import java.util.LinkedList;
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
