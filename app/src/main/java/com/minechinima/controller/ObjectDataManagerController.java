package com.minechinima.controller;

import com.minechinima.controller.factories.ControllerFactory;
import com.minechinima.model.BBContext;
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

    public IControllerCommand duplicateObject(BaseObject obj) {
        return new IControllerCommand() {
            @Override
            protected void execute() throws UserException {
                BaseObject copy = new BaseObject(obj.getIdentifier(), ObjectDataManagerController.this.owner.database);
                copy.fromData(obj.toData());
                ObjectDataManagerController.this.owner.add(copy);
            }

            @Override
            public List<BaseStructure> getAffectedNodes() {
                return List.of();
            }
        };
    }

    /**
     * duplicates Object and sets as active
     * @param context
     * @return
     */
    public IControllerCommand duplicateActiveObject(BBContext context) {
        return new IControllerCommand() {
            @Override
            protected void execute() throws UserException {
                BaseObject obj = context.activeObject.get();
                BaseObject copy = new BaseObject(obj.getIdentifier(), ObjectDataManagerController.this.owner.database);
                copy.fromData(obj.toData());
                ObjectDataManagerController.this.owner.add(copy);
                context.activeObject.set(copy);
            }

            @Override
            public List<BaseStructure> getAffectedNodes() {
                return List.of();
            }
        };
    }
}
