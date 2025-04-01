package com.minechinima.utils;

import org.joml.Vector2d;

public class ImmutableVector2d {
    private final double x;
    private final double y;

    public ImmutableVector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public ImmutableVector2d(Vector2d vec) {
        this(vec.x, vec.y);
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public Vector2d getVector2d() {
        return new Vector2d(this.x, this.y);
    }
}

