package com.minechinima.input.keymap.builder;

import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.utils.ImmutableVector2d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

public class MouseDragKeyMapBuilderTest {
    @Test
    public void calculateDirection1PixelMovement() {
        MouseDragKeyMapBuilder builder = new MouseDragKeyMapBuilder();
        LinkedList<ImmutableVector2d> positions = new LinkedList<>();

        for (int i = 0; i <= 15; i++) {
            positions.add(new ImmutableVector2d(0, i));
        }
        Assertions.assertEquals(KeyMapMouseMoveEvent.Direction.DOWN, builder.calculateDirection(positions));

        positions.clear();
        for (int i = 0; i <= 15; i++) {
            positions.add(new ImmutableVector2d(0, 15-i));
        }
        Assertions.assertEquals(KeyMapMouseMoveEvent.Direction.UP, builder.calculateDirection(positions));

        positions.clear();
        for (int i = 0; i <= 15; i++) {
            positions.add(new ImmutableVector2d(i, 0));
        }
        Assertions.assertEquals(KeyMapMouseMoveEvent.Direction.RIGHT, builder.calculateDirection(positions));

        positions.clear();
        for (int i = 0; i <= 15; i++) {
            positions.add(new ImmutableVector2d(15-i, 0));
        }
        Assertions.assertEquals(KeyMapMouseMoveEvent.Direction.LEFT, builder.calculateDirection(positions));

        positions.clear();
        for (int i = 0; i <= 15; i++) {
            positions.add(new ImmutableVector2d(i, 15-i));
        }
        Assertions.assertEquals(KeyMapMouseMoveEvent.Direction.UP_RIGHT, builder.calculateDirection(positions));

        positions.clear();
        for (int i = 0; i <= 15; i++) {
            positions.add(new ImmutableVector2d(i, i));
        }
        Assertions.assertEquals(KeyMapMouseMoveEvent.Direction.DOWN_RIGHT, builder.calculateDirection(positions));

        positions.clear();
        for (int i = 0; i <= 15; i++) {
            positions.add(new ImmutableVector2d(15-i, 15-i));
        }
        Assertions.assertEquals(KeyMapMouseMoveEvent.Direction.UP_LEFT, builder.calculateDirection(positions));

        positions.clear();
        for (int i = 0; i <= 15; i++) {
            positions.add(new ImmutableVector2d(15-i, i));
        }
        Assertions.assertEquals(KeyMapMouseMoveEvent.Direction.DOWN_LEFT, builder.calculateDirection(positions));
    }

    @Test
    public void calculateDirectionCurvedMotion() {
        MouseDragKeyMapBuilder builder = new MouseDragKeyMapBuilder();
        LinkedList<ImmutableVector2d> positions = new LinkedList<>();

        positions.add(new ImmutableVector2d(0,0));
        positions.add(new ImmutableVector2d(1,0));
        positions.add(new ImmutableVector2d(2,0));
        positions.add(new ImmutableVector2d(3,1));
        positions.add(new ImmutableVector2d(4,2));
        positions.add(new ImmutableVector2d(5,4));
        positions.add(new ImmutableVector2d(6,5));
        positions.add(new ImmutableVector2d(6,6));
        positions.add(new ImmutableVector2d(6,7));
        positions.add(new ImmutableVector2d(6,8));
        positions.add(new ImmutableVector2d(6,9));
        positions.add(new ImmutableVector2d(6,10));

        Assertions.assertEquals(KeyMapMouseMoveEvent.Direction.DOWN, builder.calculateDirection(positions));
    }
}
