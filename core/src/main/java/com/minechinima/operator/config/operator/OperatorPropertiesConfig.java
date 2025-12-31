package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.*;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Wrapper of {@link OperatorProperties}
 * with logic to deserialize/serialize and sync with a pointer to an {@link OperatorType}
 */
//TODO combine with OperatorMacroProperties as they are duplicates
public class OperatorPropertiesConfig extends BaseSingleValueHierarchy<OperatorProperties> {
    private BaseType<?> cachedData;
    private final ValuePointer<OperatorType> ptr;

    public OperatorPropertiesConfig(String id, ValuePointer<OperatorType> ptr) {
        super(id);
        this.ptr = ptr;
        this.ptr.getEventBus().subscribe(this);
    }

    @SubscribeEvent(type= SubscribeEvent.Type.ON_NODE)
    private void onValueChange(ValueChangeEvent event) {
        if (event.getTrigger() == this.ptr) {
            this.reloadProps();
        }
    }

    /**
     * @return true if the value needs to be set by calling the reload controller function.
     */
    public boolean requiresReload() {
        return this.cachedData != null;
    }

    /**
     * Reload properties and ensures they are aligned with the current operator type pointer.
     * @return
     */
    protected void reloadProps() {
        if (this.ptr.get() instanceof IOTPropertyProvider propOT) {
            this.set(propOT.createProps());
        } else {
            this.set(null);
        }
    }

    @Override
    protected OperatorProperties instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        if (!(this.ptr.get() instanceof IOTPropertyProvider<?,?> propertyProvider)) return null;
        return propertyProvider.createProps();
    }

    @Override
    public void fromData(BaseType<?> data) {
        this.cachedData = data;
        super.fromData(data);
    }

    @Override
    public void set(OperatorProperties cfg) {
        super.set(cfg);
        this.cachedData = null;
    }


}
