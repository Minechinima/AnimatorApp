package com.minechinima.operator.ot;

import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.config.IReleasableModule;
import com.minechinima.operator.ot.commands.IModalOperator;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import org.jetbrains.annotations.Nullable;

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
    default Set<? extends SwitchType<C>> switchToTypes() {
        return Set.of();
    }

    class SwitchType<C extends IContext> {
        protected final OperatorType<C, ?> type;

        public SwitchType(OperatorType<C, ?> ot) {
            this.type = ot;
        }

        public OperatorType<C, ?> getType() {
            return this.type;
        }
    }

    /**
     * This allows an operator type to define own logic how the new properties will look,
     * e.g. copy parts from the previous properties.
     * @param <C>
     * @param <P>
     */
    class SwitchTypeProperties<C extends IContext, P extends OperatorProperties> extends SwitchType<C> {
        /**
         * If the operatortype can take properties as input
         * the engine will try to copy the previous properties to
         * the new ones.
         */
        protected final boolean takePropsFromPrevious;

        public <OP extends IPropertyOperator<C, P>,
                OT extends OperatorType<C, OP> & IOTPropertyProvider<OP, P>>
        SwitchTypeProperties(OT ot, boolean takePropsFromPrevious) {
            super(ot);
            this.takePropsFromPrevious = takePropsFromPrevious;
        }

        public <OP extends IPropertyOperator<C, P>,
                OT extends OperatorType<C, OP> & IOTPropertyProvider<OP, P>>
        SwitchTypeProperties(OT ot) {
            this(ot, false);
        }

        @Nullable
        public P createProps(OperatorProperties prev) {
            P newProps = ((IOTPropertyProvider<?, P>) this.type).createProps();

            if (this.takePropsFromPrevious) {
                newProps.copyFrom(prev);
            }

            return newProps;
        }
    }
}
