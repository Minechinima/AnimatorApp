package com.minechinima.ui.input.events;

public class IOMouseEvent extends IOEvent {
    private final int mouseScroll = 0;
    private final Input mouseKey = null;

    protected IOMouseEvent(int mouseX, int mouseY, int mouseMoveX, int mouseMoveY) {
        super(mouseX, mouseY, mouseMoveX, mouseMoveY);
    }
}
