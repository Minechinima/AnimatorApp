package com.minechinima.operator.ot;

import com.minechinima.operator.config.IReleasableModule;
import com.minechinima.operator.ot.commands.IModalOperator;

import java.util.Set;

/**
 * Interface for OperatorTypes providing a Modal Operator.
 * <br><br>
 * Modal specific flags.
 * @param <O>
 */
public interface IOTModal<O extends IModalOperator<?>> extends IReleasableModule {
    O getOperator();

    /**
     * @return true if by default the hotkey configuration should enable releasing the key to confirm.
     */
    boolean confirmOnReleaseDefault();
    /**
     * A set of other OperatorTypes that can replace this
     * while this is running in modal mode.
     */
    default Set<OperatorType<?, ?>> switchToTypes() {
        return Set.of();
    }
}
