package com.minechinima.operator;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.ui.annotation.IUIHandler;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIVectorElement;

public class TranslateProps extends OperatorProperties {
    @InGui(entrypoint = TranslatePropsUIHandler.class, translationPrefix = "value", as= UIVectorElement.class)
    public final Vector3fValue value = new Vector3fValue("value");
    @InGui(entrypoint = TranslatePropsUIHandler.class, translationPrefix = "local", as= UICheckBox.class)
    public final BooleanValue local = new BooleanValue("local");

    public TranslateProps(String id) {
        super(id);
        this.addChild("value", this.value);
        this.addChild("local", this.local);
    }

    public interface TranslatePropsUIHandler extends IUIHandler<TranslateProps> {

    }
}
