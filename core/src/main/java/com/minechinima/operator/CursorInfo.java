package com.minechinima.operator;

/**
 * The only input device information the operator is allowed to access.
 * @param x
 * @param y
 * @param moveX
 * @param moveY
 */
public record CursorInfo(int x, int y, int moveX, int moveY) {
}
