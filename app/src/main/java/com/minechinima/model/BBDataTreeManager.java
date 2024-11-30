package com.minechinima.model;

import com.minechinima.model.events.EventTypeRegistry;

public class BBDataTreeManager extends DataTreeManager<BBNameSpace> {
    public BBDataTreeManager(EventTypeRegistry eventRegistry) {
        super(new BBNameSpace("bb"), eventRegistry);
        this.getRoot().initChildren();
    }
}
