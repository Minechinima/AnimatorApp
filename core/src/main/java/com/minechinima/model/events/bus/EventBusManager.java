package com.minechinima.model.events.bus;

import com.minechinima.model.base.DataTreeManager;

import java.util.LinkedList;
import java.util.Queue;


public class EventBusManager {
    private boolean buffering;
    private Queue<Runnable> eventCalls = new LinkedList<>();

    public EventBusManager() {
    }

    public boolean isBuffering() {
        return this.buffering;
    }

    public void bufferEventCall(Runnable eventMethod) {
        if (!this.buffering) throw new UnsupportedOperationException("EventBusManager is not buffering currently.");
        this.eventCalls.add(eventMethod);
    }

    public void clearBuffer() {
        this.eventCalls.clear();
    }

    public void stopBuffering() {
        this.buffering = false;
    }

    public void startBuffering() {
        this.buffering = true;
    }

    public void flush() {
        for (Runnable eventCall : this.eventCalls) {
            eventCall.run();
        }
    }
}