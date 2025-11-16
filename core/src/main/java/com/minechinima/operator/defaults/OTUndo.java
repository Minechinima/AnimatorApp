package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.OperatorType;
import org.jetbrains.annotations.NotNull;

public class OTUndo extends OperatorType<IContext, UndoOperator> {
    public OTUndo(String id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public boolean poll(IContext context) throws UserException {
        return context.getUIContext().manager().getOperatorManager().canUndo();
    }

    @Override
    public @NotNull UndoOperator getOperator() {
        return new UndoOperator();
    }
}
