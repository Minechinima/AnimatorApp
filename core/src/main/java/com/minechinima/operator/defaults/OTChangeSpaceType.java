package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;
import org.jetbrains.annotations.NotNull;

public class OTChangeSpaceType extends OperatorTypePropertyProvider<ChangeSpaceTypeProp, ChangeSpaceTypeOperator, IContext> {
    public OTChangeSpaceType(String id, String title, String description) {
        super(id, title, description, true);
    }

    @Override
    public ChangeSpaceTypeOperator getOperator(ChangeSpaceTypeProp props) {
        return new ChangeSpaceTypeOperator(props);
    }

    @Override
    public ChangeSpaceTypeProp createProps() {
        return new ChangeSpaceTypeProp( null);
    }

    @Override
    public boolean poll(IContext context) throws UserException {
        return context.getUIContext().clickedUIScreen() != null;
    }

    @Override
    public @NotNull ChangeSpaceTypeOperator getOperator() {
        return new ChangeSpaceTypeOperator(new ChangeSpaceTypeProp());
    }
}
