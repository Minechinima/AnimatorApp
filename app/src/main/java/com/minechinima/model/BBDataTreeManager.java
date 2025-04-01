package com.minechinima.model;

import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.model.events.EventTypeRegistry;

public class BBDataTreeManager extends DataTreeManager<BBNameSpace> {
    public BBDataTreeManager(EventTypeRegistry eventRegistry, HotKeyConfigRegistry configRegistry) {
        super(new BBNameSpace("bb", configRegistry), eventRegistry);
        this.getRoot().initChildren();
    }
}
