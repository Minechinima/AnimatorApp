package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.AxisEnumValueStruct;
import com.minechinima.operator.config.modal.PropertyModifierConfig;
import com.minechinima.operator.modal.IPropertyModifierCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class TranslateModalAxis extends PropertyModifierConfig<TranslateProps> {
    private final AxisEnumValueStruct.Axis axisToSet;

    public TranslateModalAxis(AxisEnumValueStruct.Axis axisToSet) {
        super("Axis", "");
        this.axisToSet = axisToSet;
    }

    @Override
    public IPropertyModifierCommand<TranslateProps, ?> factory() {
        return new IPropertyModifierCommand<>(this) {
            @Override
            public Queue<IControllerCommand> onEvent(TranslateProps props) {
                if (props.axis.get() == this.modifierConfig.axisToSet) {
                    return new LinkedList<>(List.of(props.transformOnAxis.controller().set(false)));
                }

                Queue<IControllerCommand> cmds = new LinkedList<>();
                cmds.add(props.transformOnAxis.controller().set(true));
                cmds.add(props.axis.controller().set(this.modifierConfig.axisToSet));

                return cmds;
            }
        };
    }
}
