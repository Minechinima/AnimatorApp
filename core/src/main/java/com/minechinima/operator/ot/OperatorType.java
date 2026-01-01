package com.minechinima.operator.ot;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.config.operator.OperatorPropertiesConfig;
import com.minechinima.operator.ot.commands.IModalOperator;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;

/**
 * Seperate the OperatorType from the command Operator logic for cleaner code.
 * @param <C> type of the Operator Context.
 * @implNote By defining {@link #poll(C)} here
 *           it allows generic bounds and type safety with the Context type of {@link IOperator <C>}.
 */
public abstract class OperatorType<C extends IContext, O extends IOperator<C>> extends ValueGroup {
    public final String title;
    public final String description;
    /**
     * A Queue of OperatorTypes that will be invoked after this is finished.
     * This allows creating a chain and reusing more atomic Operators.
     * <br><br>
     * Uses set to impose a limitation that an operator type cannot be used more than once.
     * This allows to build the config and use the operatortype id as key for better future proofing
     * for deserializiation if an operator changes macros. {@link OperatorPropertiesConfig.PropertyConfig}
     */
    private final LinkedHashSet<Macro<C>> macros = new LinkedHashSet<>();
    /**
     * Whether this OperatorType, when successfully finished, should be pushed to history.
     */
    private final boolean pushUndo;

    public OperatorType(String id, String title, String description, LinkedHashSet<Macro<C>> macros, boolean pushUndo) {
        super(id);
        this.title = title;
        this.description = description;
        this.pushUndo = pushUndo;
        if (macros != null) this.macros.addAll(macros);
    }

    public OperatorType(String id, String title, String description, boolean pushUndo) {
        this(id, title, description, null, pushUndo);
    }

    public OperatorType(String id, String title, String description, LinkedHashSet<Macro<C>> macros) {
        this(id, title, description, macros, false);
    }

    public OperatorType(String id, String title, String description) {
        this(id, title, description, false);
    }

    public boolean pushUndo() {
        return this.pushUndo;
    }

    @NotNull
    public LinkedHashSet<Macro<C>> getMacros() {
        return new LinkedHashSet<>(this.macros);
    }

    /**
     * Whether the Operator can be executed in the given context.
     * @param context
     * @return false if the Operator cannot be executed.
     * @throws UserException with a meaningful message, if it should be displayed.
     * @implNote This is declared in the OperatorType to avoid unnecessary instantiation of an Operator or OperatorProperties.
     */
    public abstract boolean poll(C context) throws UserException;

    @NotNull
    public abstract O getOperator();

    public static class Macro<C extends IContext> {
        private final OperatorType<C, ?> ot;
        @Nullable
        private final OperatorProperties props;
        private final boolean cancelMacroOnModalCancel;

        public <OP extends IPropertyOperator<C, P>,
                OT extends OperatorType<C, OP> & IOTPropertyProvider<OP, P>,
                P extends OperatorProperties>
        Macro(OT ot, P props) {
            this.ot = ot;
            this.props = props;
            this.cancelMacroOnModalCancel = false;
        }

        public <OP extends IPropertyOperator<C, P> & IModalOperator<C>,
                OT extends OperatorType<C, OP> & IOTPropertyProvider<OP, P> & IOTModal<OP>,
                P extends OperatorProperties>
        Macro(OT ot, P props, boolean cancelMacroOnModalCancel) {
            this.ot = ot;
            this.props = props;
            this.cancelMacroOnModalCancel = cancelMacroOnModalCancel;
        }


        public <OT extends OperatorType<C, ?>> Macro(OT ot) {
            this.ot = ot;
            this.props = null;
            this.cancelMacroOnModalCancel = false;
        }

        public <OP extends IOperator<C> & IModalOperator<C>,
                OT extends OperatorType<C, OP> & IOTModal<OP>> Macro(OT ot, boolean cancelMacroOnModalCancel) {
            this.ot = ot;
            this.props = null;
            this.cancelMacroOnModalCancel = cancelMacroOnModalCancel;
        }

        public OperatorType<C, ?> getOperatorType() {
            return ot;
        }

        public @Nullable OperatorProperties getProps() {
            return props;
        }

        public boolean isCancelMacroOnModalCancel() {
            return cancelMacroOnModalCancel;
        }
    }
}
