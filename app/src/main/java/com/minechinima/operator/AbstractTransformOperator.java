package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.OrientationEnumValueStruct;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.operator.ot.commands.flags.OpModalFlag;
import com.minechinima.ui.panels.UIViewport;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;

/**
 * Abstract base for transform operators (translate, rotate, scale).
 * Subclasses define which object property to modify and how mouse input maps to value changes.
 */
public abstract class AbstractTransformOperator<P extends TransformProps>
        extends IPropertyOperator<BBOperatorContext, P>
        implements IRepeatableOperator<BBOperatorContext>, IModalOperator<BBOperatorContext> {

    protected Vector3f startValue;
    protected UIViewport viewport;

    public AbstractTransformOperator(P props) {
        super(props);
    }

    /**
     * @return the Vector3fValue on the object that this operator modifies.
     */
    protected abstract Vector3fValue getTargetValue(BaseObject object);

    /**
     * Maps mouse movement to changes in {@code this.props.value}.
     */
    protected abstract void applyMouseInput(BBOperatorContext context, KeyMapMouseMoveEvent moveEvent);

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpInvokeFlag.CANCELLED);

        if (context.getUIContext().clickedUISpace() instanceof UIViewport vp) {
            this.viewport = vp;
        }

        BaseObject object = context.bbContext().activeObject.get();
        this.startValue = getTargetValue(object).asVector3f();

        if (this.props.orientation.get() == OrientationEnumValueStruct.Orientation.LOCAL) {
            Vector3f rot = object.rotation.asVector3f();
            Matrix3f rotM = new Matrix3f();
            rotM.rotateX((float) Math.toRadians(rot.x));
            rotM.rotateY((float) Math.toRadians(rot.y));
            rotM.rotateZ((float) Math.toRadians(rot.z));
            this.props.orientationMatrix.set(rotM);
        } else {
            this.props.orientationMatrix.set(new Matrix3f());
        }

        return new OPReturnValue<>(OpInvokeFlag.MODAL_RUNNING);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> execute(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpExecuteFlag.CANCELLED);
        BaseObject object = context.bbContext().activeObject.get();
        Vector3fValue target = getTargetValue(object);
        Vector3f offset = computeOffset();

        return new OPReturnValue<>(OpExecuteFlag.FINISHED, target.controller().set(offset.add(this.startValue)));
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return this.execute(context);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> repeat(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpExecuteFlag.CANCELLED);
        this.startValue = getTargetValue(context.bbContext().activeObject.get()).asVector3f();
        return this.execute(context);
    }

    @NotNull
    @Override
    public OPReturnValue<Modal> modal(BBOperatorContext context, KeyMapEvent event) throws UserException {
        if (event instanceof KeyMapMouseMoveEvent moveEvent) {
            applyMouseInput(context, moveEvent);
        }

        BaseObject object = context.bbContext().activeObject.get();
        Vector3fValue target = getTargetValue(object);
        Vector3f offset = computeOffset();

        IControllerCommand cmd = target.controller().set(offset.add(this.startValue));
        return new OPReturnValue<>(new Modal(OpModalFlag.MODAL_RUNNING), cmd);
    }

    protected Vector3f computeOffset() {
        Vector3f offset = this.props.value.asVector3f();
        offset.mul(this.props.scaleFactor.get());

        boolean cx = this.props.constraintX.get();
        boolean cy = this.props.constraintY.get();
        boolean cz = this.props.constraintZ.get();
        if (cx || cy || cz) {
            if (!cx) offset.x = 0;
            if (!cy) offset.y = 0;
            if (!cz) offset.z = 0;
        }

        Matrix3f orientationMatrix = this.props.orientationMatrix.asMatrix3f();
        orientationMatrix.transform(offset);

        return offset;
    }
}
