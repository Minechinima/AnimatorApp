package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.*;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.OperatorType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Wrapper of {@link OperatorPropertiesGroupStruct}
 * with logic to deserialize/serialize and sync with a pointer to an {@link OperatorType}
 */
public class OperatorConfigPropsTreeStruct extends BaseMutableValueHierarchy<OperatorPropertiesGroupStruct> {
    @Nullable
    private OperatorPropertiesGroupStruct props;
    private BaseType<?> cachedData;
    private final ValuePointer<OperatorType> ptr;

    public OperatorConfigPropsTreeStruct(String id, ValuePointer<OperatorType> ptr) {
        super(id, ValuePath.Format.PATH);
        this.ptr = ptr;
        this.ptr.getEventBus().subscribe(this);
    }

    @SubscribeEvent(type= SubscribeEvent.Type.ON_NODE)
    private void onValueChange(ValueChangeEvent event) {
        if (event.getTrigger() == this.ptr) {
            this.reloadProps(true);
        }
    }

    @Nullable
    public OperatorPropertiesGroupStruct getProps() {
        this.reloadProps(false);
        return this.props;
    }

    /**
     * Reload properties and ensures they are aligned with the current operator type pointer.
     * @return
     */
    protected void reloadProps(boolean force) {
        if (!(this.ptr.getReference() instanceof IOTPropertyProvider<?>) && this.props != null) {
            this.removeChild(this.props);
        }

        if (this.ptr.getReference() instanceof IOTPropertyProvider<?> propProvider
                && (force || this.props == null)) {
            this.props = propProvider.createProps();
            this.props.initChildren();
            try {
                this.props.setParent(this);
            } catch (SetParentNotAllowed e) {
                this.props = null;
                throw new UnsupportedOperationException(e);
            }
        }

        if (this.cachedData != null && this.props != null) {
            this.props.fromData(this.cachedData);
            this.cachedData = null;
        }
    }

    @Override
    public boolean removeChild(OperatorPropertiesGroupStruct child) {
        if (this.props == null || this.props != child) return false;
        OperatorPropertiesGroupStruct props = this.props;
        this.props = null;
        try {
            props.setParent(null);
        } catch (SetParentNotAllowed e) {
            this.props = props;
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public Collection<OperatorPropertiesGroupStruct> getChildren() {
        return this.props != null ? List.of(this.props) : List.of();
    }

    @Override
    public boolean isDirectChild(BaseStructure child) {
        return child == this.props;
    }

    @Override
    public BaseType<?> toData() {
        return this.props != null ? this.props.toData() : null;
    }

    @Override
    public void fromData(BaseType<?> data) {
        this.cachedData = data;
        this.reloadProps(true);
    }
}
