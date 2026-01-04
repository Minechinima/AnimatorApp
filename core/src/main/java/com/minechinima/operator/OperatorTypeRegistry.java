package com.minechinima.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseMutableValueHierarchy;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.defaults.OTChangeSpaceType;
import com.minechinima.operator.defaults.OTDragValueSlider;
import com.minechinima.operator.defaults.OTRedo;
import com.minechinima.operator.defaults.OTUndo;
import com.minechinima.operator.ot.OperatorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

//TODO this shouldn't be a map, as a map deserializes and instantiates
//TODO C generic weird in the registries and configs what about UIOperatorTypes and UIOperatorContext?
/**
 * Registry of all OperatorTypes
 * <br><br>
 * Part of data model to allow id management, access via paths and config attributes of OperatorTypes.
 * @param <C>
 */
public class OperatorTypeRegistry<C extends IContext> extends BaseValueMap<OperatorType<C, ?>> {
    private static final ValuePath.IFormat FORMAT = new ValuePath.IFormat() {
        @Override
        public String getStart() {
            return "(";
        }

        @Override
        public String getEnd() {
            return ")";
        }

        @Override
        public ValuePath.Format.Mode getMode() {
            return ValuePath.Format.Mode.CONCAT;
        }

        @Override
        public boolean isValidStringForEnclosure(String value) {
            return true;
        }
    };

    static {
        ValuePath.IFormat.REGISTRY.add(FORMAT);
    }

    public static final OTDragValueSlider dragUISlider = new OTDragValueSlider("drag_ui_slider", "drag_ui_slider", "Dragging a UI slider");
    public static final OTUndo undo = new OTUndo("undo", "Undo", "undo");
    public static final OTRedo redo = new OTRedo("redo", "Redo", "redo");
    public static final OTChangeSpaceType changeSpaceType = new OTChangeSpaceType("change_space_type", "Switch Space type", "change panel");

    public OperatorTypeRegistry(String id) {
        super(id, FORMAT);
        this.add((OperatorType<C, ?>) this.dragUISlider);
        this.add((OperatorType<C, ?>) this.undo);
        this.add((OperatorType<C, ?>) this.redo);
        this.add((OperatorType<C, ?>) this.changeSpaceType);
    }

    @Override
    protected @NotNull OperatorType<C, ?> instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        return null;
    }
}
