package com.minechinima.operator;

import com.minechinima.model.AxisEnumValueStruct;

public class TranslateModalAxisY extends TranslateModalAxis {
    @Override
    public void onEvent(TranslateProps original, TranslateProps props) {
        if (props.axis.get() == AxisEnumValueStruct.Axis.Y) {
            props.transformOnAxis.set(false);
            return;
        }

        props.transformOnAxis.set(true);
        props.axis.set(AxisEnumValueStruct.Axis.Y);
    }

    @Override
    public void onStopEvent(TranslateProps original, TranslateProps props) {

    }
}
