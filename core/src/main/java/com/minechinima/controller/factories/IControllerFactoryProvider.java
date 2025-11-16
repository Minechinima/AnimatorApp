package com.minechinima.controller.factories;

/**
 * Enforce the design paradigm, that the data objects need to provide a separate controller factory.
 * @param <F> the ControllerFactory.<br>
 *           <b>When implementing this, do not bind the generic type parameter of {@link ControllerFactory}!</b>
 *           Binding will make extending, overriding {@link #controller()} or implementing multiple interfaces that extend this harder
 *           because of clashes with the type parameter.<br>
 *           The factory provider does not need to declare the type of the factory owner, as it is not important for the API.
 *           It is sufficient to declare the type of {@link ControllerFactory} as it will have it's own generics for command creation..
 */
public interface IControllerFactoryProvider<F extends ControllerFactory<?>> {
    F controller();
}
