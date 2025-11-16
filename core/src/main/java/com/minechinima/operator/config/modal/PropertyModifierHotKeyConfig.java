package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Configuration for a {@link PropertyModifierConfig}
 * @param <P>
 */
public class PropertyModifierHotKeyConfig<P extends OperatorPropertiesGroupStruct> extends ValueGroup {
    public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
    public final PropertyModifierSelectorEnumStruct<P> modifier;
    public final BaseSingleValueHierarchy<HotKeyConfigSelectorEnumStruct> modalReleaseHotKeySelector = new BaseSingleValueHierarchy<>("release_config") {
        @Override
        protected HotKeyConfigSelectorEnumStruct instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
            HotKeyConfigSelectorEnumStruct config = new HotKeyConfigSelectorEnumStruct(id, PropertyModifierHotKeyConfig.this.hotKeyConfigRegistry);
            config.fromData(data);
            return config;
        }
    };

    private final HotKeyConfigRegistry hotKeyConfigRegistry;

    public PropertyModifierHotKeyConfig(PropertyModifiersListStruct<P> category) {
        super("");
        this.hotKeyConfigRegistry = category.hotKeyConfigRegistry;
        this.addChild("hotkey_config",
                this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", category.hotKeyConfigRegistry));
        this.addChild("modifier_config", this.modifier = new PropertyModifierSelectorEnumStruct<>("modifier_config", category,null));
        this.addChild("modal_confirm_config", this.modalReleaseHotKeySelector);
    }

    @Override
    protected void onEvent(Event event) {
        if (event instanceof ValueChangeEvent valueChangeEvent) {
            if (valueChangeEvent.getTrigger() == this.modifier) {
                this.onModifierTypeChange();
            } else if (valueChangeEvent.getTrigger() == this.hotKeyConfigSelector ||
                    valueChangeEvent.getTrigger().isChildOf(this.hotKeyConfigSelector)) {
                this.onHotKeyConfigChange();
            }
        }
    }

    protected void onModifierTypeChange() {
        if (this.modifier.get() != null) {
            /*
             * new OT supports modal but last didn't (as last had no config),
             * otherwise we would need more logic to somehow get old value
             */
            if (this.modifier.get() instanceof PropertyModifierReleaseableConfig<P>
                    && this.modalReleaseHotKeySelector.get() == null
                    && this.hotKeyConfigSelector.get() != null) {
                HotKeyConfigGroupStruct releaseConfig = this.hotKeyConfigSelector.get().getReleaseConfig();
                if (releaseConfig == null) return;

                this.modalReleaseHotKeySelector.set(new HotKeyConfigSelectorEnumStruct("", this.hotKeyConfigRegistry));
                this.modalReleaseHotKeySelector.get().set(releaseConfig);
            } else if (!(this.modifier.get() instanceof PropertyModifierReleaseableConfig<P>)) {
                this.modalReleaseHotKeySelector.set(null);
            }
        }

        if (this.modifier.get() == null) {
            this.modalReleaseHotKeySelector.set(null);
        }
    }

    protected void onHotKeyConfigChange() {
        if (this.hotKeyConfigSelector.get() != null
                && this.modifier.get() != null
                && this.modifier.get() instanceof PropertyModifierReleaseableConfig<P>) {
            HotKeyConfigGroupStruct releaseConfig = this.hotKeyConfigSelector.get().getReleaseConfig();
            if (releaseConfig == null) {
                this.modalReleaseHotKeySelector.set(null);
                return;
            }

            this.modalReleaseHotKeySelector.set(new HotKeyConfigSelectorEnumStruct("", this.hotKeyConfigRegistry));
            this.modalReleaseHotKeySelector.get().set(releaseConfig);
        }

        if (this.hotKeyConfigSelector.get() == null) {
            this.modalReleaseHotKeySelector.set(null);
        }
    }
}
