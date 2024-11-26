package com.minechinima.model.logging.operations;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.datainterfaces.IValueMap;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.utils.ID;

public class MapValueAdd implements IDataOperation<IValueMap<?>> {
    private final ID id;

    public MapValueAdd(BaseStructure addedValue) {
        this.id = addedValue.getID();
    }

    @Override
    public boolean performOperation(IValueMap<?> context) throws UserException {
        return false;
    }

    @Override
    public boolean undo(IValueMap<?> map) throws UserException {
        if (this.id.uuid() != null && map.canRemove(this.id.uuid())) {
            map.remove(this.id.uuid());
            return true;
        } else if (map.canRemove(this.id.id())) {
            map.remove(this.id.id());
            return true;
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
