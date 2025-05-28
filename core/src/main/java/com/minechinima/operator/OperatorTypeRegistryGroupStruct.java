package com.minechinima.operator;

import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.defaults.OTDragValueSlider;
import com.minechinima.operator.defaults.OTRedo;
import com.minechinima.operator.defaults.OTUndo;

//TODO make mutable so we can register at runtime and ensure correct Context type
public class OperatorTypeRegistryGroupStruct<C extends IContext> extends ValueGroup {
    public final OTDragValueSlider dragUISlider = new OTDragValueSlider("drag_ui_slider", "drag_ui_slider", "Dragging a UI slider");
    public final OTUndo undo = new OTUndo("undo", "Undo", "undo");
    public final OTRedo redo = new OTRedo("redo", "Redo", "redo");

    public OperatorTypeRegistryGroupStruct(String id) {
        super(id);
        this.addChild("drag_ui_slider", this.dragUISlider);
        this.addChild("undo", this.undo);
        this.addChild("redo", this.redo);
    }
}
