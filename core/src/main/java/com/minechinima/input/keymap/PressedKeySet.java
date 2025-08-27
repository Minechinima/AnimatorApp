package com.minechinima.input.keymap;

import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyType;
import com.minechinima.input.raw.Key;
import com.minechinima.utils.ImmutableVector2d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A set of Keys that are held down / pressed by storing the KeyEvent that was triggered.
 * The event instance is directly stored which allows custom event data to persist in this Set.
 * <br><br>
 * Every KeyEvent has a list of mouse positions, which allows keeping track
 * of where the mouse position moved while pressing a key.
 * Use {@link #appendMousePos(double, double)} to add a mouse position
 * <br><br>
 * The Set will not store KeyEvents that are triggered by a key that is already in the Set.
 * This means that this Set contains unique keys.
 */
public class PressedKeySet implements Set<KeyMapKeyEvent> {
    /**
     * Pressed keys and a list of their mouse positions. The first position is the position where the event
     * was first triggered.
     */
    private final Map<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> keys = new HashMap<>();

    public PressedKeySet() {

    }

    public PressedKeySet(PressedKeySet c) {
        this.keys.putAll(c.keys);
    }

    /* +-----------------------------+
     * | PRESS utility check methods |
     * +-----------------------------+ */

    public boolean ctrlPressed() {
        return this.leftctrlPressed() || this.rightctrlPressed() || this.getEventByKey(Key.MOD_CONTROL) != null;
    }

    public boolean leftctrlPressed() {
        return this.getEventByKey(Key.KEY_LEFT_CONTROL) != null;
    }

    public boolean rightctrlPressed() {
        return this.getEventByKey(Key.KEY_RIGHT_CONTROL) != null;
    }

    public boolean altPressed() {
        return this.leftaltPressed() || this.rightaltPressed() || this.getEventByKey(Key.MOD_ALT) != null;
    }

    public boolean leftaltPressed() {
        return this.getEventByKey(Key.KEY_LEFT_ALT) != null;
    }

    public boolean rightaltPressed() {
        return this.getEventByKey(Key.KEY_RIGHT_ALT) != null;
    }

    public boolean shiftPressed() {
        return this.leftshiftPressed() || this.rightshiftPressed() || this.getEventByKey(Key.MOD_SHIFT) != null;
    }

    public boolean leftshiftPressed() {
        return this.getEventByKey(Key.KEY_LEFT_ALT) != null;
    }

    public boolean rightshiftPressed() {
        return this.getEventByKey(Key.KEY_RIGHT_ALT) != null;
    }







    /**
     * Adds the given mouse position to all pressed keys.
     */
    public void appendMousePos(double mouseX, double mouseY) {
        for (Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> entry : this.keys.entrySet()) {
            entry.getValue().add(new ImmutableVector2d(mouseX, mouseY));
        }
    }

    /**
     * @return copy of the positions
     */
    public Map<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> positions() {
        Map<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> map = new HashMap<>();

        for (Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> entry : this.keys.entrySet()) {
            map.put(entry.getKey(), new LinkedList<>(entry.getValue()));
        }

        return map;
    }

    @Nullable
    public KeyMapKeyEvent getEventByKey(Key key) {
        for (KeyMapKeyEvent event : this.keys.keySet()) {
            if (event.key() == key) return event;
        }
        return null;
    }

    @Override
    public int size() {
        return this.keys.size();
    }

    @Override
    public boolean isEmpty() {
        return this.keys.isEmpty();
    }

    /**
     * @return true if a key event with the equal key is in the set.
     */
    @Override
    public boolean contains(Object o) {
        if (!(o instanceof KeyMapKeyEvent event)) return false;
        for (KeyMapKeyEvent search : this.keys.keySet()) {
            if (search.equalKey(event)) return true;
        }
        return false;
    }

    @Override
    public @NotNull Iterator<KeyMapKeyEvent> iterator() {
        return this.keys.keySet().iterator();
    }

    @Override
    public @NotNull Object[] toArray() {
        return this.keys.keySet().toArray();
    }

    @Override
    public @NotNull <T> T[] toArray(@NotNull T[] a) {
        return this.keys.keySet().toArray(a);
    }

    /**
     * Adds only events that are press/repeat type.
     * Only events that are not already present are added. Use {@link #replaceEqualKeys(KeyMapKeyEvent)} to replace keys.
     * @return true if it was added.
     */
    @Override
    public boolean add(KeyMapKeyEvent keyEvent) {
        if (!this.canBeAdded(keyEvent)) return false;
        this.keys.put(keyEvent, new LinkedList<>(List.of(new ImmutableVector2d(keyEvent.mouseX(), keyEvent.mouseY()))));
        return true;
    }

    /**
     * Removes all events triggered by the equal key.
     * @param event
     */
    public void removeEqualKeys(KeyMapKeyEvent event) {
        Iterator<Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>>> it = this.keys.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> entry = it.next();
            if (entry.getKey().equalKey(event)) {
                it.remove();
                this.keys.remove(entry.getKey());
            }
        }
    }

    /**
     * Replaces the event instance triggered by the equal key, while keeping the mouse positions.
     * @param event
     */
    public void replaceEqualKeys(KeyMapKeyEvent event) {
        Iterator<Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>>> it = this.keys.entrySet().iterator();
        Map<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> temp = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> entry = it.next();
            if (entry.getKey().equalKey(event)) {
                it.remove();
                this.keys.remove(entry.getKey());
                temp.put(event, entry.getValue());
            }
        }
        this.keys.putAll(temp);
    }

    protected boolean canBeAdded(KeyMapKeyEvent event) {
        if ((event.type() != KeyType.PRESS && event.type() != KeyType.REPEAT) || this.contains(event)) return false;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return this.keys.remove(o) != null;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.keys.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends KeyMapKeyEvent> c) {
        boolean changed = false;
        for (KeyMapKeyEvent event : c) {
            if (!this.canBeAdded(event)) continue;
            this.add(event);
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.keys.keySet().retainAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.keys.keySet().removeAll(c);
    }

    @Override
    public void clear() {
        this.keys.clear();
    }
}
