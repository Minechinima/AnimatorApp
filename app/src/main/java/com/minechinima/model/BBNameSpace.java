package com.minechinima.model;

import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.operator.BBOperatorContext;
import com.minechinima.operator.BBOperatorTypeRegistry;
import com.minechinima.operator.config.OperatorConfigurations;

public class BBNameSpace extends BaseHierarchyRoot {
    public final BBDatabase data = new BBDatabase("data");
    public final BBOperatorTypeRegistry ops = new BBOperatorTypeRegistry("operator");
    public final OperatorConfigurations<BBOperatorContext> opConfig;

    public BBNameSpace(String id, HotKeyConfigRegistry hotKeyConfigRegistry) {
        super(id, ValuePath.Format.PATH);
        this.addChild("data", this.data);
        this.addChild("operator", this.ops);
        this.addChild("config",
                this.opConfig = new OperatorConfigurations<>("operator_configurations", hotKeyConfigRegistry, this.ops));

        this.data.initChildren();
        this.ops.initChildren();
        this.opConfig.initChildren();
    }
}
