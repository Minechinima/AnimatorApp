package com.minechinima.operator.ot.commands.flags;

import com.minechinima.controller.IControllerCommand;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The return value of the operator execution methods.
 * @param flag
 * @param commands The commands to modify the database.
 * @param <T>
 */
public record OPReturnValue<T>(T flag, @Nullable IControllerCommand commands) {
    public OPReturnValue(T flag) {
        this(flag, null);
    }
}
