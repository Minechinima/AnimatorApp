package com.minechinima.model.base;

import com.minechinima.model.events.Event;
import com.minechinima.model.events.IChildEventListener;
import com.minechinima.model.events.IEventListener;

import java.util.function.Consumer;

public class EventHelper<T extends Event> implements IEventListener<T>, IChildEventListener<T> {
    private final Consumer<T> callback;

    public EventHelper(Consumer<T> callback) {
        this.callback = callback;
    }

    public void onEvent(T event) {
        this.callback.accept(event);
    }

    public void onChildEvent(T event) {
        this.callback.accept(event);
    }
}
