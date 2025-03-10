package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.events.KeyMapKeyEvent;
import com.minechinima.input.keymap.events.KeyMapMouseDragEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.input.raw.MouseEvent;
import com.minechinima.utils.ImmutableVector2d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.*;

public class MouseMoveKeyMapBuilder extends AbstractKeyMapEventBuilder<MouseEvent, KeyMapMouseMoveEvent> implements IMousePosEventProvider {
    protected Double prevMouseX;
    protected Double prevMouseY;

    @Override
    protected boolean isArgNull() {
        return super.isArgNull() || this.prevMouseX == null || this.prevMouseY == null;
    }

    @Override
    public void reset() {
        super.reset();
        this.prevMouseX = null;
        this.prevMouseY = null;
    }

    @Override
    public void setMousePos(double prevMouseX, double prevMouseY, double currentMouseX, double currentMouseY) {
        this.mouseX = currentMouseX;
        this.mouseY = currentMouseY;
        this.prevMouseX = prevMouseX;
        this.prevMouseY = prevMouseY;
    }

    private KeyMapMouseMoveEvent.Direction calculateDirection(double currentX, double currentY, double prevX, double prevY) {
        return this.calculateDirection(new Vector2d(currentX - prevX, prevY - currentY));
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
    public @Nullable Collection<KeyMapMouseMoveEvent> getResult() {
        if (this.isArgNull()) return null;
        List<KeyMapMouseMoveEvent> dragEvents = new LinkedList<>();

        dragEvents.add(new KeyMapMouseMoveEvent(this.mouseX, this.mouseY,
                this.prevMouseX, this.prevMouseY, this.window, this.pressedKeySet));

        for (Map.Entry<KeyMapKeyEvent, LinkedList<ImmutableVector2d>> pressedPositions : this.pressedKeySet.positionSet()) {
            if (pressedPositions.getValue().size() < 2) {
                System.out.println("Unexpected Pressed key position list size");
                continue;
            }
            final int dragAverageFrames = 4;
            Vector2d drag = this.getAveragedMovement(pressedPositions.getValue(), dragAverageFrames);
            if (drag.length() >= dragAverageFrames - 1) {
                drag.y = -drag.y;
                dragEvents.add(new KeyMapMouseDragEvent(pressedPositions.getKey().key(), this.mouseX, this.mouseY,
                        this.prevMouseX, this.prevMouseY, this.calculateDirection(drag), this.window, this.pressedKeySet));
            }
        }

        return dragEvents;
    }

    public Vector2d getAveragedMovement(LinkedList<ImmutableVector2d> positions, int frames) {
        //TODO better formula would be: get every nth pixel move
        Vector2d pos0 = positions.get(Math.max(0, positions.size() - frames)).getVector2d();
        Vector2d pos1 = positions.getLast().getVector2d();
        return pos1.sub(pos0);
    }

    @Override
    public Vector2d getMousePos() {
        return this.event == null ? null : new Vector2d(this.event.getX(), this.event.getY());
    }
}
