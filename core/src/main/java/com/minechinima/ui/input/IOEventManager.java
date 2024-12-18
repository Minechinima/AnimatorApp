package com.minechinima.ui.input;

import com.minechinima.ui.input.events.IOKeyboardEvent;
import com.minechinima.ui.input.events.Input;
import imgui.internal.ImGui;

import java.util.HashSet;
import java.util.Set;

public class IOEventManager {
    private Set<IOEventListener> listeners = new HashSet<>();

    public void registerListener(IOEventListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(IOEventListener listener) {
        this.listeners.remove(listener);
    }

    public void dispatch() {
        IOKeyboardEvent event = null;
        if (ImGui.isKeyPressed(Input.KEY_DELETE.imguiCode())) {
            event = new IOKeyboardEvent(Input.KEY_DELETE);
        } else if (ImGui.isKeyPressed(Input.KEY_Z.imguiCode())) {
            event = new IOKeyboardEvent(Input.KEY_Z);
        } else if (ImGui.isKeyPressed(Input.KEY_Y.imguiCode())) {
            event = new IOKeyboardEvent(Input.KEY_Y);
        }

        if (event == null) return;

        for (IOEventListener listener : this.listeners) {
            listener.onIOEvent(event);
            listener.onKeyIOEvent(event);
        }
    }
}
