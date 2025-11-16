package com.minechinima.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ControllerUtils {
    public static Queue<IControllerCommand> asQueue(IControllerCommand... commands) {
        return new LinkedList<>(List.of(commands));
    }
}
