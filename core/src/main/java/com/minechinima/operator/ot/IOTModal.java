package com.minechinima.operator.ot;

import com.minechinima.operator.config.IReleasableModule;
import com.minechinima.operator.ot.commands.IModalOperator;

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
}
