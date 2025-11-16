package com.minechinima.input.hotkey;

import com.minechinima.controller.factories.ValueControllerFactory;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.AbstractEnumValueHierarchyReg;
import com.minechinima.model.values.base.BaseValue;
import com.minechinima.utils.IGenericRegistry;

/**
 * Acts like an enum where the value is the HotKey configuration.
 */
public class HotKeyConfigSelectorEnumStruct extends AbstractEnumValueHierarchyReg<HotKeyConfigGroupStruct> {
    public HotKeyConfigSelectorEnumStruct(String id, IGenericRegistry<HotKeyConfigGroupStruct> registry) {
        super(id, registry,null);
    }
}
