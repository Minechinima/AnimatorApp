package com.minechinima.model;

import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.operator.BBOperatorContext;
import com.minechinima.operator.BBOperatorTypeRegistryGroupStruct;
import com.minechinima.operator.config.KeyConfigurationsGroupStruct;

public class BBNameSpace extends BaseHierarchyRoot {
    public final BBDatabase data = new BBDatabase("data");
    public final BBOperatorTypeRegistryGroupStruct ops = new BBOperatorTypeRegistryGroupStruct("operator");
    public final KeyConfigurationsGroupStruct<BBOperatorContext> opConfig;

    public BBNameSpace(String id, HotKeyConfigRegistry hotKeyConfigRegistry) {
        super(id, ValuePath.Format.PATH);
        this.addChild("data", this.data);
        this.addChild("operator", this.ops);
        this.addChild("config",
                this.opConfig = new KeyConfigurationsGroupStruct<>("operator_configurations", hotKeyConfigRegistry, this.ops));

        this.data.initChildren();
        this.ops.initChildren();
        this.opConfig.initChildren();
    }
}
