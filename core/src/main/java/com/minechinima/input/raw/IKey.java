package com.minechinima.input.raw;

/**
 * Interface for a Key object.
 */
public interface IKey {
    /**
     * @return keycode of this key.
     * @implNote Might not be unique, see GAMEPAD and MOUSE overlapping keycodes.
     */
    int keyCode();
    default boolean equalsKey(IKey key) {
        //TODO do we need equalsKey? equalsKey would not guarantee commmutativity a.equalsKey(b) == b.equalsKey(a)
        return key.keyCode() == this.keyCode();
    }
}
