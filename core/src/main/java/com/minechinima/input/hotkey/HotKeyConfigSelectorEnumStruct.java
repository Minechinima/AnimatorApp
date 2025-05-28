package com.minechinima.input.hotkey;

import com.minechinima.model.values.AbstractEnumValueHierarchyReg;

/**
 * Acts like an enum where the value is the HotKey configuration.
 */
public class HotKeyConfigSelectorEnumStruct extends AbstractEnumValueHierarchyReg<HotKeyConfigGroupStruct> {
    public HotKeyConfigSelectorEnumStruct(String id, HotKeyConfigRegistry registry) {
        super(id, registry, registry.getNewDefault());
    }
}
