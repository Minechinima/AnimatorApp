package com.minechinima.operator.ot;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

/**
 * Utility class for an operator type that just declares macro operators
 */
public class MacroOperatorType<C extends IContext> extends OperatorType<C, IOperator<C>> {
    public MacroOperatorType(String id, String title, String description, boolean pushUndo, Macro<C>... macros) {
        super(id, title, description, macroVarArgsToSet(macros), pushUndo);
    }

    private static <C extends IContext> LinkedHashSet<Macro<C>> macroVarArgsToSet(Macro<C>... macros) {
        if (macros == null || macros.length == 0) {
            return new LinkedHashSet<>();
        }

        LinkedHashSet<Macro<C>> result = new LinkedHashSet<>();
        for (Macro<C> macro : macros) {
            if (macro != null) {
                result.add(macro);
            }
        }
        return result;
    }

    @Override
    public boolean poll(C context) throws UserException {
        /* returns true by default as the macro operator types are also polled */
        return true;
    }

    @Override
    public @NotNull IOperator<C> getOperator() {
        return context -> new OPReturnValue<>(OpInvokeFlag.FINISHED);
    }
}
