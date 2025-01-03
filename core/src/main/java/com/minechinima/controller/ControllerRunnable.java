package com.minechinima.controller;

import com.minechinima.model.exceptions.UserException;

@FunctionalInterface
@Deprecated
public interface ControllerRunnable {
    void run() throws UserException;
}
