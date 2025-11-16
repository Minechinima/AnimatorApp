package com.minechinima.model;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.base.BaseValueList;
import com.minechinima.operator.BBOperatorContext;
import com.minechinima.operator.BBOperatorTypeRegistryGroupStruct;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BBNameSpace extends BaseHierarchyRoot {
    public final BBDatabase data = new BBDatabase("data");
    public final BBOperatorTypeRegistryGroupStruct ops = new BBOperatorTypeRegistryGroupStruct("operator");
    public final UserFunctionalityConfigGroupStruct<BBOperatorContext> opConfig;
    public final BaseValueList<BBContext> contexts = new BaseValueList<BBContext>("all_contexts") {
        @NotNull
        @Override
        protected BBContext instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
            BBContext ctx = new BBContext(id, BBNameSpace.this.data);
            ctx.fromData(map);
            return ctx;
        }
    };

    public BBNameSpace(String id, HotKeyConfigRegistry hotKeyConfigRegistry) {
        super(id, ValuePath.Format.PATH);
        this.addChild("data", this.data);
        this.addChild("operator", this.ops);
        this.addChild("config",
                this.opConfig = new UserFunctionalityConfigGroupStruct<>("operator_configurations", hotKeyConfigRegistry, this.ops));
        this.addChild("contexts", this.contexts);

        this.data.initChildren();
        this.opConfig.initChildren();
    }
}
