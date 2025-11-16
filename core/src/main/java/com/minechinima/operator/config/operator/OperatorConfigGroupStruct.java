package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.NullType;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import com.minechinima.operator.ot.IOTModal;
import com.minechinima.operator.ot.OperatorType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Configuration for a single operator type hotkey.
 * @param <C>
 */
public class OperatorConfigGroupStruct<C extends IContext> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final ValuePointer<OperatorType> operatorType;
    public final OperatorConfigPropsTreeStruct props;
    public final BaseSingleValueHierarchy<OperatorModalConfirmConfig> modalConfirmConfig = new BaseSingleValueHierarchy<>("modal_confirm_config") {
        @Override
        protected OperatorModalConfirmConfig instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
            OperatorModalConfirmConfig config = new OperatorModalConfirmConfig(OperatorConfigGroupStruct.this.hotKeyConfigRegistry);
            config.fromData(data);
            return config;
        }
    };

    private final HotKeyConfigRegistry hotKeyConfigRegistry;

    public OperatorConfigGroupStruct(UserFunctionalityConfigGroupStruct<C> opConfig) {
        super("");
        this.hotKeyConfigRegistry = opConfig.hotKeyConfigRegistry;

        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", this.hotKeyConfigRegistry));
        this.addChild("operator_type",
                this.operatorType = new ValuePointer<>("operator_type", OperatorType.class, opConfig.operatorTypeRegistry));
        this.addChild("properties",
                this.props = new OperatorConfigPropsTreeStruct("properties", this.operatorType));
        this.addChild("modal_confirm_config", this.modalConfirmConfig);
    }

    @Override
    protected void onEvent(Event event) {
        if (event instanceof ValueChangeEvent valueChangeEvent) {
            if (valueChangeEvent.getTrigger() == this.operatorType) {
                this.onOperatorTypeChange();
            } else if (valueChangeEvent.getTrigger().isChildOf(this.hotKeyConfigSelector)
                    || valueChangeEvent.getTrigger() == this.hotKeyConfigSelector) {
                this.onHotKeyConfigChange();
            }
        }
    }

    protected void onOperatorTypeChange() {
        if (this.operatorType.getReference() instanceof IOTModal<?> modalOT) {
            /*
             * new OT supports modal but last didn't (as last had no config),
             * otherwise we would need more logic to somehow get old value
             */
            if (this.modalConfirmConfig.get() == null && this.hotKeyConfigSelector.get() != null) {
                HotKeyConfigGroupStruct releaseConfig = this.hotKeyConfigSelector.get().getReleaseConfig();
                if (releaseConfig == null) return;

                this.modalConfirmConfig.set(new OperatorModalConfirmConfig(modalOT.confirmOnReleaseByDefault(), this.hotKeyConfigRegistry));
                this.modalConfirmConfig.get().hotKeyConfigSelector.set(releaseConfig);
            } else if (this.modalConfirmConfig.get() != null) {
                this.modalConfirmConfig.get().confirmOnRelease.set(modalOT.confirmOnReleaseByDefault());
            }
        } else {
            this.modalConfirmConfig.set(null);
        }
    }

    protected void onHotKeyConfigChange() {
        if (this.hotKeyConfigSelector.get() != null && this.operatorType.getReference() instanceof IOTModal<?> modalOT) {
            HotKeyConfigGroupStruct releaseConfig = this.hotKeyConfigSelector.get().getReleaseConfig();
            if (releaseConfig == null) {
                this.modalConfirmConfig.set(null);
                return;
            }

            this.modalConfirmConfig.set(new OperatorModalConfirmConfig(modalOT.confirmOnReleaseByDefault(), this.hotKeyConfigRegistry));
            this.modalConfirmConfig.get().hotKeyConfigSelector.set(releaseConfig);
        } else {
            this.modalConfirmConfig.set(null);
        }
    }
}
