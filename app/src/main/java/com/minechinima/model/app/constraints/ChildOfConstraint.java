package com.minechinima.model.app.constraints;

public class ChildOfConstraint extends ConstraintStruct {
    public ChildOfConstraint(String id) {
        super(id);
    }

    @Override
    public String title() {
        return "child_of";
    }
}
