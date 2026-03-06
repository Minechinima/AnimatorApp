package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.AbstractEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.events.types.changes.ValueAddEvent;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.OperatorTypeRegistry;
import com.minechinima.operator.ot.IOTModalModifierProvider;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This datastructure holds all the modifier configurations for each property.
 * Each item in this map is a configuration for all the modifiers of one property type.
 */
public class PropertyModifierConfigsMapStruct extends BaseValueMap<PropertyModifiersList<?>> {
    private final HashMap<Class<OperatorProperties>, PropertyModifiersList<?>> cache = new HashMap<>();
    private final OperatorTypeRegistry operatorRegistry;
    private final IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry;

    public PropertyModifierConfigsMapStruct(String id, OperatorTypeRegistry operatorRegistry, IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry) {
        super(id);
        this.operatorRegistry = operatorRegistry;
        this.hotKeyConfigRegistry = hotKeyConfigRegistry;
        this.operatorRegistry.getChildren().forEach(ot -> {
            if (ot instanceof IOTModalModifierProvider provider) this.addPropertyModifierConfig(provider);
        });
        this.operatorRegistry.getEventBus().subscribe(new Object() {
            @SubscribeEvent(type = SubscribeEvent.Type.ON_NODE)
            private void onAddOT(ValueAddEvent event) {
                if (event.getAddedElement() instanceof IOTModalModifierProvider provider) {
                    addPropertyModifierConfig(provider);
                }
            }
        });
    }

    @Override
    protected void onChildEvent(AbstractEvent event) {
        if (event instanceof AddToTreeEvent addToTreeEvent
                && addToTreeEvent.getTrigger() instanceof PropertyModifiersList modifier) {
            this.cache.put(modifier.clazz, modifier);
        } else if (event instanceof RemoveFromTreeEvent removeFromTreeEvent
                && removeFromTreeEvent.getTrigger() instanceof PropertyModifiersList modifier) {
            this.cache.remove(modifier.clazz);
        }
    }

    private <P extends OperatorProperties> void addPropertyModifierConfig(IOTModalModifierProvider<?, ?, P> provider) {
        final String registryID = provider.modifierConfigIdentifier();
        final PropertyModifiersList<P> list;
        final PropertyModifiersList search;
        if ((search = this.get(registryID)) != null) {
            if (search.clazz != provider.getPropertyType() || search.registry != provider.modifierRegistry()) {
                return;
            } else {
                list = (PropertyModifiersList<P>) search;
            }
        } else {
            list = new PropertyModifiersList<>(registryID, provider.getPropertyType(), provider.modifierRegistry(), this.hotKeyConfigRegistry);
        }

        // TODO this is not a necessary invariant solve step so data stays valid.
        //  the logic for this needs to be moved to controller
        //TODO how do we map modal keymap to OperatorTypes? identifier not secure, what if identifier was already registered
        // the identifier in the OT declaration acts as a template currently.
        list.addOperatorType(provider);
        this.add(list);
    }

    @Override
    protected @NotNull PropertyModifiersList instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        //TODO property modifier should be a map, as we might want to add operators later on at runtime
        // however, it cant instantiate, it is a datastructure that is set by code. We cant create a modifier list
        // as we do not have the registry instances.
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
    public <P extends OperatorProperties> List<PropertyModifierHotKeyConfig<? extends P>> getConfigs(P props, IOTModalModifierProvider<?, ?, ? extends P> operatorType) {
        List<PropertyModifierHotKeyConfig<? extends P>> list = new ArrayList<>();
        for (PropertyModifiersList config : this.getChildren()) {
            if (config.clazz.isAssignableFrom(props.getClass()) && config.operatorsUsingThis.contains(operatorType)) {
                //TODO a system so configs of subclasses override the configs of superclasses
                list.addAll(config.getChildren());
            }
        }

        return list;
    }

    public <P extends OperatorProperties> Set<PropertyModifiersList<P>> getConfigCategoriesForDirect(Class<P> clazz) {
        PropertyModifiersList<P> search = (PropertyModifiersList<P>) this.cache.get(clazz);
        return search == null ? Set.of() : Set.of(search);
    }
}
