package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.AbstractEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.OperatorProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Each item in this map is a configuration for all the modifiers of one property type.
 */
public class PropertyModifierConfigsMapStruct extends BaseValueMap<PropertyModifiersListStruct<?>> {
    private final HashMap<Class<OperatorProperties>, PropertyModifiersListStruct<?>> cache = new HashMap<>();

    public PropertyModifierConfigsMapStruct(String id) {
        super(id);
    }

    @Override
    protected void onChildEvent(AbstractEvent event) {
        if (event instanceof AddToTreeEvent addToTreeEvent
                && addToTreeEvent.getTrigger() instanceof PropertyModifiersListStruct modifier) {
            this.cache.put(modifier.clazz, modifier);
        } else if (event instanceof RemoveFromTreeEvent removeFromTreeEvent
                && removeFromTreeEvent.getTrigger() instanceof PropertyModifiersListStruct modifier) {
            this.cache.remove(modifier.clazz);
        }
    }

    @Override
    protected @NotNull PropertyModifiersListStruct instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        return null;
    }

    //TODO currently property modifier is solely mapped to properties. Use the OTModalMOdifierProvider identifier or something
    // to allow registering multiple modifier configs per property and retrieve them for the operator type.
    // operatortype still is the last instance of control -> allow OT to declare whether it even uses modal modifier keymap
    // and perhaps if it uses a different e.g. multiple modal keymaps for one property, but for multiple operatortypes.
    /**
     *
     * @param props
     * @return all property modifier configurations that are assignable to the property class or subclasses.
     * @param <P>
     */
    public <P extends OperatorProperties> List<PropertyModifierHotKeyConfig<P>> getConfigs(P props) {
        List<PropertyModifierHotKeyConfig<P>> list = new ArrayList<>();
        for (PropertyModifiersListStruct config : this.getChildren()) {
            if (config.clazz.isAssignableFrom(props.getClass())) {
                //TODO go through the modifiers and replace configs from subclasses where the modifier class is equal
                // this allows for configurations for subclasses of props to overwrite modifier behavior of the superclass
                list.addAll(config.getChildren());
            }
        }

        return list;
    }

    public <P extends OperatorProperties> Set<PropertyModifiersListStruct<P>> getConfigCategoriesForDirect(Class<P> clazz) {
        PropertyModifiersListStruct<P> search = (PropertyModifiersListStruct<P>) this.cache.get(clazz);
        return search == null ? Set.of() : Set.of(search);
    }
}
