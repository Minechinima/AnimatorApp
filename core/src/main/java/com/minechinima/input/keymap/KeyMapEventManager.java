package com.minechinima.input.keymap;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyType;
import com.minechinima.input.raw.IInputEventListener;
import com.minechinima.input.raw.InputEvent;
import com.minechinima.input.raw.InputType;
import com.minechinima.ui.UIManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is the interface for the raw hardware input and the application keymap events.
 */
public class KeyMapEventManager implements IInputEventListener<InputEvent> {
    private final Multimap<Class<? extends KeyMapEvent>, IKeyMapEventListener<? extends KeyMapEvent>> listeners = LinkedListMultimap.create();
    /**
     * Dynamic factory for converting one {@link InputEvent} class into a {@link KeyMapEvent}.
     * 1:1 mapping for input events.
     */
    private final Map<Class<? extends InputEvent>, IKeyMapEventBuilder<? extends InputEvent, ? extends KeyMapEvent>> registry = new HashMap<>();
    private double mouseX = 0;
    private double mouseY = 0;
    private double prevMouseX = 0;
    private double prevMouseY = 0;
    private final PressedKeySet pressedKeys = new PressedKeySet();
    private final UIManager<?> manager;
    /**
     * Map of KeyEvents that were released and not consumed, meaning they were clicked, mapped to the time
     * for triggering a double click.
     */
    private final Map<KeyMapKeyEvent, Long> releasedKeys = new HashMap<>();
    private double doubleClickmsTimer = 350;

    public KeyMapEventManager(UIManager<?> manager) {
        this.manager = manager;
    }

    public <T extends KeyMapEvent> void addListener(Class<T> clazz, IKeyMapEventListener<T> listener) {
        this.listeners.put(clazz, listener);
    }

    public void removeListener(IKeyMapEventListener listener) {
        this.listeners.values().remove(listener);
    }

    public <T extends KeyMapEvent> void removeListener(Class<T> clazz, IKeyMapEventListener<T> listener) {
        this.listeners.get(clazz).remove(listener);
    }

    public <T extends InputEvent> void registerKeyMapBuilder(Class<T> inputEventClass, IKeyMapEventBuilder<T, ?> builder) {
        this.registry.put(inputEventClass, builder);
    }

    @Override
    public void onEvent(InputEvent event) {
        IKeyMapEventBuilder builder;
        if ((builder = this.registry.get(event.getClass())) == null) return;

        builder.onEvent(event);

        /* event converter provides mouse position */
        if (builder instanceof IMousePosEventProvider mousePosProvider) {
            Vector2d pos = mousePosProvider.getMousePos();
            this.prevMouseX = this.mouseX;
            this.prevMouseY = this.mouseY;
            this.mouseX = pos.x;
            this.mouseY = pos.y;
            this.pressedKeys.appendMousePos(this.mouseX, this.mouseY);
        }

        builder.setMousePos(this.prevMouseX, this.prevMouseY, this.mouseX, this.mouseY);
        //TODO imgui generates new os windows for popups, these are not in the UI framework atm
        builder.setUIWindow(this.manager.getWindow(event.getWindow()));

        if (builder instanceof IKeyEventBuilder keyEventBuilder) {
            keyEventBuilder.setKeyMapType(this.convertInputTypeKeyType(keyEventBuilder.getInputType()));
        }

        builder.setPressedKeys(new PressedKeySet(this.pressedKeys));

        Collection<KeyMapEvent> results = builder.getResult();

        if (results == null) return;

        for (KeyMapEvent result : results) {
            this.handleEvent(builder, result);
        }

        builder.reset();
        this.cleanupCachedReleasedKeys();
    }

    private void handleEvent(IKeyMapEventBuilder builder, KeyMapEvent result) {
        boolean consumed = this.notifyListeners(result);

        if (!(result instanceof KeyMapKeyEvent keyEvent)) return;

        switch (keyEvent.type()) {
            case PRESS, REPEAT -> {
                if (this.pressedKeys.contains(keyEvent)) {
                    this.pressedKeys.replaceEqualKeys(keyEvent);
                } else {
                    this.pressedKeys.add(keyEvent);
                }
            } default -> {
                this.pressedKeys.removeEqualKeys(keyEvent);
            }
        }

        if (keyEvent.type() == KeyType.RELEASE && !consumed) {
            /* a released keyevent that was not consumed becomes a click event, or double-click */
            KeyMapKeyEvent prevClick = this.getReleasedKeyByEqualKey(keyEvent);

            Collection<? extends KeyMapKeyEvent> clicks = null;

            if (builder instanceof IKeyEventBuilder<?, ?> keyEventBuilder) {
                keyEventBuilder.setPressedKeys(new PressedKeySet(this.pressedKeys));
                //TODO possible flaw(?): a keyEventBuilder that procudes multiple release events
                // -> for every release event there could be produced n amount of the same click events ->
                // the getResult method is called for every original getResult... potentially creating a duplicate batch of events?
                if (prevClick != null && System.currentTimeMillis() - this.releasedKeys.get(prevClick) <= this.doubleClickmsTimer) {
                    keyEventBuilder.setKeyMapType(KeyType.DOUBLECLICK);
                    clicks = keyEventBuilder.getResult();
                } else if (prevClick == null) {
                    keyEventBuilder.setKeyMapType(KeyType.CLICK);
                    clicks = keyEventBuilder.getResult();
                }
            }

            if (clicks != null) {
                for (KeyMapKeyEvent click : clicks) {
                    if (!this.notifyListeners(click) && prevClick == null) {
                        this.releasedKeys.put(keyEvent, System.currentTimeMillis());
                    } else {
                        this.releasedKeys.remove(prevClick);
                    }
                }
            }
        }
    }

    private void cleanupCachedReleasedKeys() {
        Iterator<Map.Entry<KeyMapKeyEvent, Long>> it = this.releasedKeys.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<KeyMapKeyEvent, Long> entry = it.next();
            if (System.currentTimeMillis() - entry.getValue() > this.doubleClickmsTimer + 100) {
                it.remove();
            }
        }
    }

    @Nullable
    private KeyMapKeyEvent getReleasedKeyByEqualKey(KeyMapKeyEvent target) {
        for (KeyMapKeyEvent key : this.releasedKeys.keySet()) {
            if (key.equalKey(target)) return key;
        }
        return null;
    }

    @NotNull
    private KeyType convertInputTypeKeyType(InputType type) {
        switch (type) {
            case PRESS -> {
                return KeyType.PRESS;
            }
            case REPEAT -> {
                return KeyType.REPEAT;
            }
            case RELEASE -> {
                return KeyType.RELEASE;
            }
            default -> throw new IllegalArgumentException("Unknown InputType enum value");
        }
    }

    /**
     * @param event
     * @return true when one of the listeners consumed it
     */
    public boolean notifyListeners(KeyMapEvent event) {
        //TODO proper inheritance of events....
        for (IKeyMapEventListener listener : this.listeners.get(event.getClass())) {
            if (listener.onEvent(event)) return true;
        }
        for (IKeyMapEventListener listener : this.listeners.get(KeyMapEvent.class)) {
            if (listener.onEvent(event)) return true;
        }
        return false;
    }
}
