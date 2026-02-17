package com.minechinima.controller;

/**
 * Facade for controller to let data and controller interact in a minimal way.
 */
public class ControllerFacade {
    /**
     * @implNote Do not expose.
     */
    private final ControllerManager controllerManager;

    public ControllerFacade(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public boolean canMutate() {
        return this.controllerManager.isWriting();
    }
}
