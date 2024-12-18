package com.minechinima.ui.input.events;

import java.util.HashSet;
import java.util.Set;

public abstract class IOEvent {
    private final Set<Input> pressedKeys = new HashSet<>();
    private final int mouseX;
    private final int mouseY;

    private final int mouseMoveX;
    private final int mouseMoveY;

    protected IOEvent(int mouseX, int mouseY, int mouseMoveX, int mouseMoveY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseMoveX = mouseMoveX;
        this.mouseMoveY = mouseMoveY;
    }
}
