package com.minechinima.ui.input;

import com.minechinima.ui.input.events.IOEvent;
import com.minechinima.ui.input.events.IOKeyboardEvent;
import com.minechinima.ui.input.events.IOMouseEvent;

public interface IOEventListener {
    void onIOEvent(IOEvent event);
    void onMouseIOEvent(IOMouseEvent event);
    void onKeyIOEvent(IOKeyboardEvent event);
}
