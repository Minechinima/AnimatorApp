package com.minechinima.input.keymap;

import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyType;
import com.minechinima.input.raw.Key;
import com.minechinima.utils.ImmutableVector2d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.*;
import java.util.function.Predicate;

/**
 * A set of pressed Keys by storing the KeyEvent that was triggered by the press.
 * Every KeyEvent has a list of mouse positions, which allows keeping track
 * of where the mouse position moved while pressing a key.
 * The Set will not store KeyEvents that are triggered by a key that is already in the Set.
 * This means that this Set contains unique keys.
 * KeyEvents that or are not a PRESS/REPEAT Event will not be stored.
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

    /**
     * Adds the given mouse position to all pressed keys.
     */
    public void addMousePos(double mouseX, double mouseY) {
        for (Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> entry : this.keys.entrySet()) {
            entry.getValue().add(new ImmutableVector2d(mouseX, mouseY));
        }
    }

    /**
     * @return copy of the positions
     */
    public Set<Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>>> positionSet() {
        Set<Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>>> set = new HashSet<>();

        for (Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> entry : this.keys.entrySet()) {
            set.add(new AbstractMap.SimpleEntry<>(entry.getKey(), new LinkedList<>(entry.getValue())));
        }

        return set;
    }

    @Nullable
    public KeyMapKeyEvent getKey(Key key) {
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
     * Adds only events that are press type.
     * @param keyEvent element whose presence in this collection is to be ensured
     * @return
     */
    @Override
    public boolean add(KeyMapKeyEvent keyEvent) {
        if (!this.canBeAdded(keyEvent)) return false;
        this.keys.put(keyEvent, new LinkedList<>(List.of(new ImmutableVector2d(keyEvent.mouseX(), keyEvent.mouseY()))));
        return true;
    }

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
     * Replaces the event instance while keeping the mouse positions.
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

    private boolean canBeAdded(KeyMapKeyEvent event) {
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
