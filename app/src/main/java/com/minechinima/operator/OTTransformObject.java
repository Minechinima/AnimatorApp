package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.operator.ot.OperatorTypePropertyProvider;
import com.minechinima.operator.ot.commands.IModalOperator;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class OTTransformObject<O extends IPropertyOperator<BBOperatorContext, P> & IModalOperator<BBOperatorContext>, P extends TransformProps>
        extends OperatorTypePropertyProvider<P, O, BBOperatorContext>
        implements IOTModal<BBOperatorContext, O> {
    protected final Set<SwitchToTransformProps<?>> switchTypes = new HashSet<>();

    public OTTransformObject(String id, String title, String description) {
        super(id, title, description, true);
    }

    public void addSwitchType(OTTransformObject<?, ?> transformObject) {
        this.switchTypes.add(new SwitchToTransformProps<>(transformObject));
    }

    public Set<SwitchToTransformProps<?>> switchToTypes() {
        return this.switchTypes;
    }

    public static class SwitchToTransformProps<P extends TransformProps> extends SwitchTypeProperties<BBOperatorContext, P> {

        public <OP extends IPropertyOperator<BBOperatorContext, P>, OT extends OperatorType<BBOperatorContext, OP> & IOTPropertyProvider<OP, P>>
        SwitchToTransformProps(OT ot) {
            super(ot, true);
        }

        @Nullable
        public P createProps(OperatorProperties prev) {
            if (!(prev instanceof TransformProps transformProps)) return null;
            P newProps = ((IOTPropertyProvider<?, P>) this.type).createProps();

            if (this.takePropsFromPrevious) {
                newProps.copyFrom(prev);
                newProps.value.x.set(0F);
                newProps.value.y.set(0F);
                newProps.value.z.set(0F);
            }

            return newProps;
        }
    }
}
