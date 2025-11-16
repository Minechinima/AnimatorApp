package com.minechinima.controller.factories;

/**
 * Enforce the design paradigm, that the data objects need to provide a separate controller factory.
 * @param <F>
 */
public interface IControllerFactoryProvider<F extends ControllerFactory> {
    F controller();
}
