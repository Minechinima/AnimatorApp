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
    private final Set<UIDragFloat> floatElements = new LinkedHashSet<>();
    private boolean selectingElements = true;

    public UIDragValueSlider(DragValueSliderProp props) {
        super(props);
    }

    @Override
    public void close() {
        for (UIDragFloat drag : this.floatElements) {
            drag.highlight = false;
        }
    }

    @Override
    public Pair<OpInvokeFlag, Queue<IControllerCommand>> invoke(UIOperatorContext context) throws UserException {
        if (context.currentHoveredWidget() instanceof UIDragFloat dragFloat) {
            this.floatElements.add(dragFloat);
            dragFloat.highlight = true;
            return new Pair<>(OpInvokeFlag.MODAL_RUNNING, null);
        }

        return new Pair<>(OpInvokeFlag.CANCELLED, null);
    }

    @Override
    public Pair<Modal, Queue<IControllerCommand>> modal(OperatorContext<UIOperatorContext> ctx, KeyMapEvent event) throws UserException {
        //TODO execute after invoke directly once
        UIOperatorContext context = ctx.current();
        if (event instanceof KeyMapMouseDragEvent mouseMoveEvent) {
            Vector2d move = new Vector2d(mouseMoveEvent.dragX(), mouseMoveEvent.dragY());
            boolean isValueChanging = mouseMoveEvent.direction() == KeyMapMouseMoveEvent.Direction.LEFT ||
                    mouseMoveEvent.direction() == KeyMapMouseMoveEvent.Direction.RIGHT;
            if (!isValueChanging && this.selectingElements && context.currentHoveredWidget() instanceof UIDragFloat dragFloat) {
                this.floatElements.add(dragFloat);
                dragFloat.highlight = true;
            } else if (Math.abs(move.x) >= 1) {
                this.selectingElements = false;
                Queue<IControllerCommand> commands = new LinkedList<>();
                this.floatElements.forEach(e -> {
                    FloatValue v = (FloatValue) e.getValue();
                    commands.add(v.controllers().set((float) (v.get() + Math.signum(mouseMoveEvent.dragX()) * this.props.incrementCTRL.get())));
                });
                return new Pair<>(new Modal(OpModalFlag.MODAL_RUNNING), commands);
            }
        }

        return new Pair<>(new Modal(OpModalFlag.MODAL_RUNNING), null);
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
        for (UIDragFloat floatElement : this.floatElements) {
            labels += floatElement.getValue().getIdentifier();
        }

        return "Set " + labels;
    }
}
