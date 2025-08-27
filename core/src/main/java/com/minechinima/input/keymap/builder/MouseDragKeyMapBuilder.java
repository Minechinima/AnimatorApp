package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyMapMouseDragEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.input.raw.KeyEvent;
import com.minechinima.input.raw.MouseEvent;
import com.minechinima.utils.ImmutableVector2d;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.LinkedList;
import java.util.Set;

public class MouseDragKeyMapBuilder extends AbstractKeyMapEventBuilder<MouseEvent, KeyMapMouseDragEvent> {
    public Vector2d getAveragedMovement(LinkedList<ImmutableVector2d> positions, int frames) {
        //TODO better formula would be: get every nth pixel move
        Vector2d pos0 = positions.get(Math.max(0, positions.size() - frames)).getVector2d();
        Vector2d pos1 = positions.getLast().getVector2d();
        return pos1.sub(pos0);
    }

    private KeyMapMouseMoveEvent.Direction calculateDirection(Vector2d drag) {
        double angle = Math.toDegrees(drag.angle(new Vector2d(0,1)));
        if (angle < 0) {
            angle = 360 + angle;
        }
        angle = Math.min(359, angle + 45 / 2D);
        return KeyMapMouseDragEvent.Direction.values()[(int) Math.floor(angle / 45)];
    }

    @Override
    public @Nullable KeyMapMouseDragEvent getResult() {
        if (this.isArgNull()) return null;

        LinkedList<ImmutableVector2d> positions;

        final int dragAverageFrames = 4;
        //Vector2d drag = this.getAveragedMovement(positions, dragAverageFrames);
        //if (drag.length() <= 4) return null;
        return null;
    }
}
