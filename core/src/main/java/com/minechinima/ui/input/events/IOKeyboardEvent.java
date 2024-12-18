package com.minechinima.ui.input.events;

public class IOKeyboardEvent extends IOEvent {
    private final Input key;

    public IOKeyboardEvent(Input key) {
        super(0,0,0,0);
        this.key = key;
    }

    public Input key() {
        return key;
    }
}
