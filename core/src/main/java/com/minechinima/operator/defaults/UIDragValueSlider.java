package com.minechinima.operator.defaults;

import com.minechinima.controller.ControllerChain;
import com.minechinima.controller.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseDragEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.UIOperatorContext;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.operator.ot.commands.flags.OpModalFlag;
import com.minechinima.ui.elements.UIDragFloat;
import com.minechinima.utils.Pair;
import org.joml.Vector2d;

import java.util.*;

public class UIDragValueSlider extends IPropertyOperator<UIOperatorContext, DragValueSliderProp>
        implements IOperator<UIOperatorContext>, IModalOperator<UIOperatorContext>, ITitleProvider<UIOperatorContext> {
    /* runtime attributes */
    private final List<Pair<Float, UIDragFloat<?>>> floatElements = new LinkedList<>();
    private boolean selectingElements = true;
    private Vector2d startPos = null;

    public UIDragValueSlider(DragValueSliderProp props) {
        super(props);
    }

    @Override
    public void close() {
        for (Pair<Float, UIDragFloat<?>> drag : this.floatElements) {
            drag.getB().highlight = false;
        }
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(UIOperatorContext context) throws UserException {
        if (context.currentHoveredWidget() instanceof UIDragFloat<?> dragFloat) {
            this.floatElements.add(new Pair<>(dragFloat.getValue().get(), dragFloat));
            dragFloat.highlight = true;
            return new OPReturnValue<>(OpInvokeFlag.MODAL_RUNNING);
        }

        return new OPReturnValue<>(OpInvokeFlag.CANCELLED);
    }

    private boolean containsUIDragFloat(UIDragFloat dragFloat) {
        for (Pair<Float, UIDragFloat<?>> pair : this.floatElements) {
            if (pair.getB() == dragFloat) return true;
        }

        return false;
    }

    @Override
    public OPReturnValue<Modal> modal(UIOperatorContext context, KeyMapEvent event) throws UserException {
        //TODO execute after invoke directly once
        if (this.startPos == null) this.startPos = new Vector2d(event.mouseX(), event.mouseY());

        if (event instanceof KeyMapMouseDragEvent mouseMoveEvent) {
            boolean isValueChanging = mouseMoveEvent.getCurrentDirection() == KeyMapMouseMoveEvent.Direction.LEFT ||
                    mouseMoveEvent.getCurrentDirection() == KeyMapMouseMoveEvent.Direction.RIGHT;
            if (!isValueChanging && this.selectingElements && context.currentHoveredWidget() instanceof UIDragFloat<?> dragFloat) {
                if (!this.containsUIDragFloat(dragFloat)) {
                    this.floatElements.add(new Pair<>(dragFloat.getValue().get(), dragFloat));
                }
                dragFloat.highlight = true;
                return new OPReturnValue<>(new Modal(OpModalFlag.MODAL_RUNNING));
            } else if (isValueChanging) {
                this.selectingElements = false;
            }
        }

        IControllerCommand commands = new ControllerChain();
        if (!this.selectingElements) {
            for (Pair<Float, UIDragFloat<?>> entry : this.floatElements) {
                FloatValue v = entry.getB().getValue();
                double value = entry.getA() + (event.mouseX() - this.startPos.x()) * this.props.increment.get();
                if (this.props.round.get()) value = Math.round(value);
                commands.next(v.controller().set((float) value));
            }
        }

        return new OPReturnValue<>(new Modal(OpModalFlag.MODAL_RUNNING), commands);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(UIOperatorContext ctx, BaseStructure changedProp) throws UserException {
        return null;
    }

    @Override
    public String getTitle(UIOperatorContext context) {
        //TODO we need more translation features like composite keys, or nesting?
        // example: "langkey.operator.set" + "label1key" + "label2key"
        String labels = "";
        for (Pair<Float, UIDragFloat<?>> floatElement : this.floatElements) {
            labels += floatElement.getB().getValue().getIdentifier();
        }

        return "Set " + labels;
    }
}
