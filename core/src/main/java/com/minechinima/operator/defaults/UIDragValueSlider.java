package com.minechinima.operator.defaults;

import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseDragEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.OperatorContext;
import com.minechinima.operator.UIOperatorContext;
import com.minechinima.operator.ot.commands.*;
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
    public Pair<OpInvokeFlag, Queue<IControllerCommand>> invoke(UIOperatorContext context) throws UserException {
        if (context.currentHoveredWidget() instanceof UIDragFloat<?> dragFloat) {
            this.floatElements.add(new Pair<>(dragFloat.getValue().get(), dragFloat));
            dragFloat.highlight = true;
            return new Pair<>(OpInvokeFlag.MODAL_RUNNING, null);
        }

        return new Pair<>(OpInvokeFlag.CANCELLED, null);
    }

    private boolean containsUIDragFloat(UIDragFloat dragFloat) {
        for (Pair<Float, UIDragFloat<?>> pair : this.floatElements) {
            if (pair.getB() == dragFloat) return true;
        }

        return false;
    }

    @Override
    public Pair<Modal, Queue<IControllerCommand>> modal(OperatorContext<UIOperatorContext> ctx, KeyMapEvent event) throws UserException {
        //TODO execute after invoke directly once
        UIOperatorContext context = ctx.current();
        if (this.startPos == null) this.startPos = new Vector2d(event.mouseX(), event.mouseY());

        if (event instanceof KeyMapMouseDragEvent mouseMoveEvent) {
            boolean isValueChanging = mouseMoveEvent.direction() == KeyMapMouseMoveEvent.Direction.LEFT ||
                    mouseMoveEvent.direction() == KeyMapMouseMoveEvent.Direction.RIGHT;
            if (!isValueChanging && this.selectingElements && context.currentHoveredWidget() instanceof UIDragFloat<?> dragFloat) {
                if (!this.containsUIDragFloat(dragFloat)) {
                    this.floatElements.add(new Pair<>(dragFloat.getValue().get(), dragFloat));
                }
                dragFloat.highlight = true;
                return new Pair<>(new Modal(OpModalFlag.MODAL_RUNNING), null);
            } else if (isValueChanging) {
                this.selectingElements = false;
            }
        }

        Queue<IControllerCommand> commands = null;
        if (!this.selectingElements) {
            commands = new LinkedList<>();
            for (Pair<Float, UIDragFloat<?>> entry : this.floatElements) {
                FloatValue v = entry.getB().getValue();
                double value = entry.getA() + (event.mouseX() - this.startPos.x()) * this.props.increment.get();
                if (this.props.round.get()) value = Math.round(value);
                commands.add(v.controllers().set((float) value));
            }
        }

        return new Pair<>(new Modal(OpModalFlag.MODAL_RUNNING), commands);
    }

    @Override
    public Pair<OpExecuteFlag, Queue<IControllerCommand>> onUIPropChange(OperatorContext<UIOperatorContext> ctx, BaseStructure changedProp) throws UserException {
        return null;
    }

    @Override
    public String getTitle(OperatorContext<UIOperatorContext> context) {
        //TODO we need more translation features like composite keys, or nesting?
        // example: "langkey.operator.set" + "label1key" + "label2key"
        String labels = "";
        for (Pair<Float, UIDragFloat<?>> floatElement : this.floatElements) {
            labels += floatElement.getB().getValue().getIdentifier();
        }

        return "Set " + labels;
    }
}
