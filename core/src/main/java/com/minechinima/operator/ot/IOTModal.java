package com.minechinima.operator.ot;

import com.minechinima.operator.IContext;
import com.minechinima.operator.config.IReleasableModule;
import com.minechinima.operator.ot.commands.IModalOperator;
import com.minechinima.operator.ot.commands.IPropertyOperator;

import java.util.Set;

/**
 * Interface for OperatorTypes providing a Modal Operator.
 * <br><br>
 * Modal specific flags.
 * @param <O>
 */
public interface IOTModal<C extends IContext, O extends IModalOperator<C>> extends IReleasableModule {
    O getOperator();

    /**
     * @return true if by default the hotkey configuration should enable releasing the key to confirm.
     */
    boolean confirmOnReleaseDefault();
    /**
     * A set of other OperatorTypes that can replace this
     * while this is running in modal mode.
     */
    default Set<SwitchToType<C>> switchToTypes() {
        return Set.of();
    }

    class SwitchToType<C extends IContext> {
        private final OperatorType<C, ?> type;
        /**
         * If the operatortype can take properties as input
         * the engine will try to copy the previous properties to
         * the new ones.
         */
        private final boolean takePropsFromPrevious;

        public <OP extends IPropertyOperator<C, ?>,
                OT extends OperatorType<C, OP> & IOTPropertyProvider<OP, ?>> SwitchToType(OT ot, boolean takePropsFromPrevious) {
            this.type = ot;
            this.takePropsFromPrevious = takePropsFromPrevious;
        }

        public SwitchToType(OperatorType<C, ?> ot) {
            this.type = ot;
            this.takePropsFromPrevious = false;
        }

        public OperatorType<C, ?> getType() {
            return this.type;
        }

        public boolean takePropsFromPrevious() {
            return this.takePropsFromPrevious;
        }
    }
}
