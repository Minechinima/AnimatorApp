package com.minechinima.input.hotkey.configs.values;

import com.minechinima.ui.utils.ILangKeyProvider;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.Nullable;

import javax.naming.OperationNotSupportedException;
import java.util.Map;
import java.util.function.Supplier;

public enum KeyType implements ILangKeyProvider {
    PRESS("Press", "Triggered when the key is pressed for the first time"),
    RELEASE("Release", "Triggered when the key is released"),
    CLICK("Click", "Triggered when the key is clicked. This is a press followed by a release, where neither pressing or release did something."),
    DOUBLECLICK("Double click", "Triggered when the key clicked twice in a time period."),
    REPEAT("Repeat", "Triggered when the key is held down repeatedly."),
    CLICKDRAG("Click Drag", "Triggered when the key is held down and the mouse moves.");

    private final String title;
    private final String tooltip;

    KeyType(String title, String tooltip) {
        this.title = title;
        this.tooltip = tooltip;
    }

    public static final IGenericRegistry<KeyType> REGISTRY = new IGenericRegistry<KeyType>() {
        @Override
        public @Nullable KeyType getNew(int id) {
            return switch (id) {
                case 0 -> PRESS;
                case 1 -> RELEASE;
                case 2 -> CLICK;
                case 3 -> DOUBLECLICK;
                case 4 -> REPEAT;
                case 5 -> CLICKDRAG;
                default -> null;
            };
        }

        @Override
        public @Nullable Integer getID(KeyType value) {
            return switch (value) {
                case PRESS -> 0;
                case RELEASE -> 1;
                case CLICK -> 2;
                case DOUBLECLICK -> 3;
                case REPEAT -> 4;
                case CLICKDRAG -> 5;
                default -> null;
            };
        }

        @Override
        public Map<Integer, KeyType> getAllNew() {
            return CollectionUtils.toMap(KeyType.values());
        }

        @Override
        public <J extends KeyType> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }
    };

    public boolean equalsKeyType(com.minechinima.input.keymap.events.KeyType type) {
        switch (type) {
            case PRESS -> {
                return this == PRESS;
            }
            case RELEASE -> {
                return this == RELEASE;
            }
            case REPEAT -> {
                return this == REPEAT;
            }
            case CLICK -> {
                return this == CLICK;
            }
            case DOUBLECLICK -> {
                return this == DOUBLECLICK;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public @Nullable String tooltip() {
        return this.tooltip;
    }
}
