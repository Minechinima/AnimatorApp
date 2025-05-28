package com.minechinima.operator;

import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.AxisEnumValueStruct;

public class TranslateModalAxisX extends TranslateModalAxis {
    @Override
    public void onEvent(TranslateProps original, TranslateProps props) {
        if (props.axis.get() == AxisEnumValueStruct.Axis.X) {
            props.transformOnAxis.set(false);
            return;
        }

        props.transformOnAxis.set(true);
        props.axis.set(AxisEnumValueStruct.Axis.X);
    }

    public void onStopEvent(TranslateProps original, TranslateProps props) {

    }
}
