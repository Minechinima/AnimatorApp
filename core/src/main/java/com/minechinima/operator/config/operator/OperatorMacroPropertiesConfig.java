package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.operator.ot.commands.IModalOperator;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Wraps {@link Macros} which contains a tree of macro properties.
 */
public class OperatorMacroPropertiesConfig extends BaseSingleValueHierarchy<OperatorMacroPropertiesConfig.Macros> {
    private BaseType<?> cachedData;
    private final ValuePointer<OperatorType> ptr;

    public OperatorMacroPropertiesConfig(String id, ValuePointer<OperatorType> ptr) {
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
        if (this.ptr.get() != null && !this.ptr.get().getMacros().isEmpty()) {
            this.set(new Macros(this.ptr.get()));
        } else {
            this.set(null);
        }
    }

    @Override
    protected @Nullable OperatorMacroPropertiesConfig.Macros instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        if (this.ptr.get() != null && !this.ptr.get().getMacros().isEmpty()) {
            return new Macros(this.ptr.get());
        } else {
            return null;
        }
    }

    @Override
    public void fromData(BaseType<?> data) {
        this.cachedData = data;
        super.fromData(data);
    }

    @Override
    public void set(Macros cfg) {
        super.set(cfg);
        this.cachedData = null;
    }

    /**
     * Wrapper for an immutable list of property configs of macro operators.
     */
    public class Macros extends ValueGroup {
        /**
         * Mapping of property config to their operator type for easier access.
         */
        private Map<OperatorType, PropertyConfig> macroPropertiesCache = new LinkedHashMap<>();

        public Macros(@NotNull final OperatorType<?, ?> operatorType) {
            super("");
            /* go through macros recursively creating a tree */
            for (OperatorType.Macro macro : operatorType.getMacros()) {
                if (!(macro.getOperatorType() instanceof IOTPropertyProvider<?,?> macroOTPropPrvder)) continue;

                OperatorProperties macroProps = macroOTPropPrvder.createProps();
                OperatorType macroOT = macro.getOperatorType();
                if (macro.getProps() != null) macroProps.copyFrom(macro.getProps());

                PropertyConfig macroConfig;
                if (macroOT instanceof IOTModal<?>) {
                    macroConfig = new ModalMacroConfig(macroOT.getIdentifier(), macroOT, macroProps);
                } else {
                    macroConfig = new PropertyConfig(macroOT.getIdentifier(), macroOT, macroProps);
                }

                this.macroPropertiesCache.put(macroOT, macroConfig);
                this.addChild(macroOT.getIdentifier(), macroConfig);
            }
        }

        public Map<OperatorType, PropertyConfig> getMacroProperties() {
            return new HashMap<>(this.macroPropertiesCache);
        }
    }
    /**
     * Wrapper for the property of an operatortype and optionally can have child nodes with the properties of macro operators.
     * @param <P>
     * @param <OT>
     */
    public class PropertyConfig<OP extends IPropertyOperator<?, P>,
            P extends OperatorProperties,
            OT extends OperatorType<?, OP> & IOTPropertyProvider<OP, P>>
            extends ValueGroup {
        public final P props;

        private Map<OperatorType, PropertyConfig> macroPropertiesCache = new LinkedHashMap<>();

        public PropertyConfig(String id, @NotNull final OT operatorType, final P props) {
            super(id);
            this.addChild("props", this.props = props);

            /* go through macros recursively creating a tree */
            for (OperatorType.Macro macro : operatorType.getMacros()) {
                if (!(macro.getOperatorType() instanceof IOTPropertyProvider<?,?> macroOTPropPrvder)) continue;

                OperatorProperties macroProps = macroOTPropPrvder.createProps();
                OperatorType macroOT = macro.getOperatorType();
                if (macro.getProps() != null) macroProps.copyFrom(macro.getProps());

                PropertyConfig macroConfig = new PropertyConfig(macroOT.getIdentifier(), macroOT, macroProps);

                this.macroPropertiesCache.put(macroOT, macroConfig);
                this.addChild(macroOT.getIdentifier(), macroConfig);
            }

        }

        public PropertyConfig(String id, @NotNull final OT operatorType) {
            this(id, operatorType, operatorType.createProps());
        }

        public Map<OperatorType, PropertyConfig> getMacroProperties() {
            return new HashMap<>(this.macroPropertiesCache);
        }
    }

    public class ModalMacroConfig<OP extends IPropertyOperator<?, P> & IModalOperator<?>,
            P extends OperatorProperties,
            OT extends OperatorType<?, OP> & IOTPropertyProvider<OP, P> & IOTModal<OP>>
            extends PropertyConfig {
        //TODO this cant work because generics are not handled / ignored in generator frame work @InGui(as = UICheckBox.class, translationPrefix = "cancel_macros")
        public final BooleanValue cancelMacros = new BooleanValue("cancel_macros", false);

        public ModalMacroConfig(String id, @NotNull OperatorType operatorType, OperatorProperties props) {
            super(id, operatorType, props);
            this.addChild("cancel_macros", this.cancelMacros);
        }
    }
}
