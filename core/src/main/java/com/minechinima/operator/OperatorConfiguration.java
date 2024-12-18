package com.minechinima.operator;

import com.minechinima.ui.input.events.Input;

import java.util.HashMap;

public class OperatorConfiguration<C extends OperatorContext> {
    public HashMap<Input, OperatorType<C, ?>> keyConfig = new HashMap<>();
}
