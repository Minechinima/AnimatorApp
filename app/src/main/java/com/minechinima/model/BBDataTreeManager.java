package com.minechinima.model;

import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.model.base.DataTreeManager;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.utils.IGenericRegistry;

public class BBDataTreeManager extends DataTreeManager<BBNameSpace> {
    public BBDataTreeManager(EventTypeRegistry eventRegistry, IGenericRegistry<HotKeyConfigGroupStruct> configRegistry) {
        super(new BBNameSpace("bb", configRegistry), eventRegistry);
        this.getRoot().initChildren();
    }
}
