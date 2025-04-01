package com.minechinima.operator;

/**
 * The context information for the operators.
 * Allows differentiating between the context the operator
 * was triggered in and the current context that might be different.
 * @param triggered
 * @param current
 * @param <C>
 */
public record OperatorContext<C extends IContext>(C triggered, C current) {
}
