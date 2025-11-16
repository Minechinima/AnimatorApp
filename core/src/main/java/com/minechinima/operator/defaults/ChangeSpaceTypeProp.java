package com.minechinima.operator.defaults;

import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.ui.elements.window.UISpace;
import org.jetbrains.annotations.Nullable;

public class ChangeSpaceTypeProp extends OperatorPropertiesGroupStruct {
    private final UISpace panel;

    public ChangeSpaceTypeProp(UISpace panel) {
        this.panel = panel;
    }

    public ChangeSpaceTypeProp() {
        this.panel = null;
    }

    /**
     * @return can be null if it was not assigned by some automated system.
     */
    @Nullable
    public UISpace getPanel() {
        return this.panel;
    }

    @Override
    public ChangeSpaceTypeProp copy() {
        ChangeSpaceTypeProp copy = new ChangeSpaceTypeProp(this.panel);
        copy.fromData(this.toData());
        return copy;
    }
}
