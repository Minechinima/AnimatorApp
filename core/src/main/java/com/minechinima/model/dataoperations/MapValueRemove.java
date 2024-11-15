package com.minechinima.model.dataoperations;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.MapType;
import com.minechinima.model.dataoperations.datainterfaces.IValueMap;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;

public class MapValueRemove implements IDataOperation<IValueMap<?>> {
    private final MapType removedElement;

    public MapValueRemove(MapType removedElement) {
        this.removedElement = removedElement;
    }

    @Override
    public boolean performOperation(IValueMap<?> context) throws UserException {
        return false;
    }

    @Override
    public boolean undo(IValueMap map) throws UserException {
        try {
            Object value = map.valueFromData(this.removedElement);
            if (map.canAdd(value)) {
                return map.add(value);
            }
        } catch (BaseTypeCannotBeParsed e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean isCorrectType(Object object) {
        return object instanceof IValueMap<?>;
    }

    @Override
    public boolean merge(IDataOperation next) {
        return false;
    }
}
