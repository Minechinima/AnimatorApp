package com.minechinima.input.hotkey.configs.values;

import com.minechinima.input.raw.IKey;
import com.minechinima.input.raw.Key;
import com.minechinima.ui.utils.ILangKeyProvider;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.OperationNotSupportedException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public enum KeyModEnum implements ILangKeyProvider, IKey {
    CONTROL(Key.KEY_LEFT_CONTROL, Key.KEY_RIGHT_CONTROL, Key.MOD_CONTROL),
    ALT(Key.KEY_LEFT_ALT, Key.KEY_RIGHT_ALT, Key.MOD_ALT),
    SHIFT(Key.KEY_LEFT_SHIFT, Key.KEY_RIGHT_SHIFT, Key.MOD_SHIFT);

    public final Set<Key> keys;

    KeyModEnum(Key... keys) {
        this.keys = Set.of(keys);
    }

    public static final IGenericRegistry<KeyModEnum> REGISTRY = new IGenericRegistry<KeyModEnum>() {
        @Override
        public @Nullable KeyModEnum getNew(int id) {
            return switch (id) {
                case 0 -> CONTROL;
                case 1 -> ALT;
                case 2 -> SHIFT;
                default -> null;
            };
        }

        @Override
        public @Nullable Integer getID(KeyModEnum value) {
            return switch (value) {
                case CONTROL -> 0;
                case ALT -> 1;
                case SHIFT -> 2;
                default -> null;
            };
        }

        @Override
        public Map<Integer, KeyModEnum> getAllNew() {
            return CollectionUtils.toMap(KeyModEnum.values());
        }

        @Override
        public <J extends KeyModEnum> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }
    };

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
        //TODO REVISE IKey interfaces
        return 0;
    }

    @Override
    public boolean equalsKey(IKey key) {
        for (Key originalKey : this.keys) {
            if (key != null && originalKey.keyCode() == key.keyCode()) return true;
        }
        return false;
    }
}
