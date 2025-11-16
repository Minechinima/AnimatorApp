package com.minechinima.operator.ot.commands.flags;

import com.minechinima.controller.IControllerCommand;

import java.util.LinkedList;
import java.util.Queue;

public record OPReturnValue<T>(T flag, Queue<IControllerCommand> commands) {
    public OPReturnValue(T flag) {
        this(flag, new LinkedList<>());
    }
}
