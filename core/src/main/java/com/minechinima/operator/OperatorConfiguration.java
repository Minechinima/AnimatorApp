package com.minechinima.operator;

import com.minechinima.operator.ot.AbstractOperatorType;
import com.minechinima.ui.input.events.Input;

import java.util.HashMap;

public class OperatorConfiguration<C extends OperatorContext> {
    public HashMap<Input, AbstractOperatorType<C, ?>> keyConfig = new HashMap<>();
    //TODO configure hotkey, modal keymap, for which UI spaces/regions, show in context menu for space..., show in ui config.
}
