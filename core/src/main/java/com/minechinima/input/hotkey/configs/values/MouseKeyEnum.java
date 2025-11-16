package com.minechinima.input.hotkey.configs.values;

import com.minechinima.input.hotkey.configs.AbstractHotKeyKeyConfigGroupStruct;
import com.minechinima.input.raw.Key;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public enum MouseKeyEnum implements AbstractHotKeyKeyConfigGroupStruct.IKeyEnum {
    MOUSE_BUTTON_1(0),
    MOUSE_BUTTON_2(1),
    MOUSE_BUTTON_3(2),
    MOUSE_BUTTON_4(3),
    MOUSE_BUTTON_5(4),
    MOUSE_BUTTON_6(5),
    MOUSE_BUTTON_7(6),
    MOUSE_BUTTON_8(7),
    MOUSE_WHEEL_UP(8),
    MOUSE_WHEEL_DOWN(9);

    public static final IGenericRegistry<MouseKeyEnum> REGISTRY = new IGenericRegistry<MouseKeyEnum>() {
        @Override
        public @Nullable MouseKeyEnum getNew(int id) {
            return fromImGuiCode(id);
        }

        @Override
        public @Nullable Integer getID(MouseKeyEnum value) {
            return value.imguiCode;
        }

        @Override
        public Map<Integer, MouseKeyEnum> getAllNew() {
            return CollectionUtils.toMap(MouseKeyEnum.values());
        }

        @Override
        public <J extends MouseKeyEnum> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }
    };

    private final int imguiCode;

    MouseKeyEnum(int imguiCode) {
        this.imguiCode = imguiCode;
    }

    public int imguiCode() {
        return this.imguiCode;
    }

    public boolean equalsRawKey(Key key) {
        return this.imguiCode == key.imguiCode();
    }

    @Nullable
    public static MouseKeyEnum fromImGuiCode(int code) {
        switch (code) {
            case 0: return MOUSE_BUTTON_1;
            case 1: return MOUSE_BUTTON_2;
            case 2: return MOUSE_BUTTON_3;
            case 3: return MOUSE_BUTTON_4;
            case 4: return MOUSE_BUTTON_5;
            case 5: return MOUSE_BUTTON_6;
            case 6: return MOUSE_BUTTON_7;
            case 7: return MOUSE_BUTTON_8;
            default: return null;
        }
    }

    @Override
    public String title() {
        return this.name();
    }

    @Override
    public @Nullable String tooltip() {
        return this.name();
    }

    @Override
    public int keyCode() {
        return this.imguiCode;
    }
}
