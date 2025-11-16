package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.AxisEnumValueStruct;
import com.minechinima.operator.config.modal.PropertyModifierConfig;
import com.minechinima.operator.modal.IPropertyModifierCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TranslateModalAxisX extends TranslateModalAxis {
    public TranslateModalAxisX() {
        super(AxisEnumValueStruct.Axis.X);
    }
}
