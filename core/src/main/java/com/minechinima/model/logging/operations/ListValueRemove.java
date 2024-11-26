package com.minechinima.model.logging.operations;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.MapType;
import com.minechinima.model.logging.datainterfaces.IValueList;
import com.minechinima.model.logging.datainterfaces.ListElement;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;

public class ListValueRemove implements IDataOperation<IValueList<?>> {
    private final MapType removedListElement;
    private final int index;

    public ListValueRemove(MapType removedListElement, int index) {
        this.removedListElement = removedListElement;
        this.index = index;
    }

    @Override
    public boolean performOperation(IValueList<?> context) throws UserException {
        return false;
    }

    @Override
    public boolean undo(IValueList<?> list) throws UserException {
        int index = Math.min(list.size(), this.index);
        try {
            ListElement listElement = list.listElementFromData(this.removedListElement);
            if (list.canAdd(index, listElement)) {
                return list.addListElement(index, listElement);
            }
        } catch (BaseTypeCannotBeParsed e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean isCorrectType(Object object) {
        return object instanceof IValueList<?>;
    }

    @Override
    public boolean merge(IDataOperation next) {
        return false;
    }
}
