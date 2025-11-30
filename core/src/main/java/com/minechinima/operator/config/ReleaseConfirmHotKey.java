package com.minechinima.operator.config;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIEnum;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Wrapper for a hotkey config for confirming something when releasing.
 * Wraps {@link Config}<br><br>
 * <b>MEMORY MANAGEMENT. This should stay in the tree as it is registered to an eventbus.</b>.
 */
public class ReleaseConfirmHotKey<T extends BaseStructure & IValue> extends BaseSingleValueHierarchy<ReleaseConfirmHotKey.Config> {
    private final HotKeyConfigSelectorEnumStruct parentHotKeySelector;
    private final T module;
    private IReleasableModule cache;

    public ReleaseConfirmHotKey(String id, HotKeyConfigSelectorEnumStruct parentHotKey, T module) {
        super(id);
        this.parentHotKeySelector = parentHotKey;
        this.module = module;
        this.parentHotKeySelector.getEventBus().subscribe(this);
        this.module.getEventBus().subscribe(this);
    }

    @SubscribeEvent(type = SubscribeEvent.Type.ALL)
    protected void onChangeEvent(ValueChangeEvent event) {
        if (event.getTrigger() == this.module) {
            this.onModuleChange();
        } else if (event.getTrigger() == this.parentHotKeySelector ||
                event.getTrigger().isChildOf(this.parentHotKeySelector)) {
            this.onHotKeyConfigChange();
        }
    }

    protected void onModuleChange() {
        if (this.module.get() != null) {
            /*
             * new OT supports modal but last didn't (as last had no config),
             * otherwise we would need more logic to somehow get old value
             */
            if (this.module.get() instanceof IReleasableModule releasableModule && this.parentHotKeySelector.get() != null) {
                HotKeyConfigGroupStruct releaseConfig;
                if ((releaseConfig = this.parentHotKeySelector.get().getReleaseConfig()) == null) return;

                Config config = new Config(releasableModule.confirmOnReleaseDefault());
                config.hotKeyConfigSelector.set(releaseConfig);

                this.set(config);
            } else if (!(this.module.get() instanceof IReleasableModule)) {
                this.set(null);
            }
        }

        if (this.module.get() == null) {
            this.set(null);
        }
    }

    protected void onHotKeyConfigChange() {
        if (this.parentHotKeySelector.get() != null && this.module.get() instanceof IReleasableModule releasableModule) {
            HotKeyConfigGroupStruct releaseConfig = this.parentHotKeySelector.get().getReleaseConfig();
            if (releaseConfig == null) {
                this.set(null);
                return;
            }
            boolean confirm = this.get() != null ? this.get().confirmOnRelease.get() : releasableModule.confirmOnReleaseDefault();
            Config config = new Config(confirm);
            config.hotKeyConfigSelector.set(releaseConfig);
            this.set(config);
        } else {
            this.set(null);
        }
    }

    @Override
    protected Config instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        Config cfg = new Config();
        cfg.fromData(data);
        return cfg;
    }

    public class Config extends ValueGroup {
        public final HotKeyConfigSelectorEnumStruct hotKeyConfigSelector;
        public final BooleanValue confirmOnRelease;

        public Config() {
            this(false);
        }

        public Config(boolean confirmOnEvent) {
            super("");
            this.addChild("hotkey_config",
                    this.hotKeyConfigSelector = new HotKeyConfigSelectorEnumStruct("hotkey_config", ReleaseConfirmHotKey.this.parentHotKeySelector.getRegistry()));
            this.addChild("confirm_on_release",
                    this.confirmOnRelease = new BooleanValue("confirm_on_event", confirmOnEvent));
        }
    }
}
