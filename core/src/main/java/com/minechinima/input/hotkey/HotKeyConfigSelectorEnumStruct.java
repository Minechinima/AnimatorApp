package com.minechinima.input.hotkey;

import com.minechinima.controller.factories.ValueControllerFactory;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.AbstractEnumValueHierarchyReg;
import com.minechinima.model.values.base.BaseValue;

/**
 * Acts like an enum where the value is the HotKey configuration.
 */
public class HotKeyConfigSelectorEnumStruct extends AbstractEnumValueHierarchyReg<HotKeyConfigGroupStruct> {
    public HotKeyConfigSelectorEnumStruct(String id, HotKeyConfigRegistry registry) {
        super(id, registry,null);
    }

    @Override
    protected HotKeyConfigGroupStruct instantiate(BaseType<?> data) {
        return null;
    }
}
