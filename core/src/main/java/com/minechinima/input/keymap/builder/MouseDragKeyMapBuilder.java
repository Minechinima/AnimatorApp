package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyMapMouseDragEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.input.raw.KeyEvent;
import com.minechinima.input.raw.MouseEvent;
import com.minechinima.utils.ImmutableVector2d;
import com.minechinima.utils.MathUtils;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.*;

public class MouseDragKeyMapBuilder extends AbstractKeyMapEventBuilder<MouseEvent, KeyMapMouseDragEvent> {
    private final LinkedList<ImmutableVector2d> positions = new LinkedList<>();
    private int dragThreshold = 10;
    /**
     * Calculates the direction based on the provided positions.
     * Assumes that index 0 is the start position.
     * @param positions
     * @return the direction of the mouse movement or null if the drag threshold was not surpassed.
     */
    @Nullable
    protected KeyMapMouseMoveEvent.Direction calculateDirection(LinkedList<ImmutableVector2d> positions) {
        final Iterator<ImmutableVector2d> it = positions.descendingIterator();
        final Vector2d reference = new Vector2d(0, -1);
        if (positions.size() < 2) return null;
        Vector2d p1 = it.next().getVector2d();
        Vector2d accumulated = new Vector2d();
        Vector2d averaged = new Vector2d();
        int count = 0;

        while (it.hasNext()) {
            Vector2d p0 = it.next().getVector2d();
            p1.sub(p0);
            accumulated.add(p1);
            accumulated.div(++count, averaged);

            if (accumulated.length() > this.dragThreshold) break;
            p1 = p0;
        }
        if (accumulated.length() <= this.dragThreshold) return null;

        double angle = -Math.toDegrees(averaged.angle(reference));
        if (angle < 0) {
            angle = 360 + angle;
        }
        angle = Math.min(359, angle + 45 / 2D);
        return KeyMapMouseDragEvent.Direction.values()[(int) Math.floor(angle / 45)];
    }

    @Override
    public @Nullable KeyMapMouseDragEvent getResult() {
        if (this.isArgNull()) return null;
        if (this.positions.isEmpty()) {
            this.positions.add(new ImmutableVector2d(this.prevMouseX, this.prevMouseY));
        }
        this.positions.add(new ImmutableVector2d(this.mouseX, this.mouseY));
        //if (this.positions.size() > this.dragThreshold + 1) this.positions.pollFirst();

        final KeyMapMouseMoveEvent.Direction currentDirection;
        if ((currentDirection = this.calculateDirection(this.positions)) == null) return null;

        Map<KeyMapKeyEvent, KeyMapMouseMoveEvent.Direction> result = new HashMap<>();
        for (Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> entry : this.pressedKeySet.positions().entrySet()) {
            final KeyMapMouseMoveEvent.Direction dir;
            if ((dir = this.calculateDirection(entry.getValue())) == null) continue;
            result.put(entry.getKey(), dir);
        }

        if (result.isEmpty()) return null;

        return new KeyMapMouseDragEvent(this.mouseX, this.mouseY, this.prevMouseX, this.prevMouseY, this.window,
                this.pressedKeySet, result, currentDirection);
    }
}
