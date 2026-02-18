package com.minechinima.operator.config;

import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.AbstractEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.events.types.changes.ValueAddEvent;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.OperatorTypeRegistry;
import com.minechinima.operator.config.modal.ModalFunctionConfigsMapStruct;
import com.minechinima.operator.config.modal.PropertyModifierConfigsMapStruct;
import com.minechinima.operator.config.modal.PropertyModifiersListStruct;
import com.minechinima.operator.config.operator.OperatorConfigCategoryGroupStruct;
import com.minechinima.operator.config.operator.OperatorConfigGroupStruct;
import com.minechinima.operator.config.operator.OperatorConfigCategoriesMapStruct;
import com.minechinima.operator.ot.IOTModalModifierProvider;
import com.minechinima.ui.elements.window.UIRegion;
import com.minechinima.ui.elements.window.UISpace;
import com.minechinima.utils.IGenericRegistry;
import com.minechinima.utils.Pair;

import java.util.*;

/**
 * This class has all the configuration data for the user functionality
 *
 */
public class UserFunctionalityConfigGroupStruct<C extends IContext> extends ValueGroup {
    //TODO we need a cache of config categories for their UI identifiers for shallow and quick search
    // when the operator manager retrieves categories for a specific ui.
    // impl idea: listener when children get addeed -> ui identifiers are final, add them here into a map.
    public final OperatorConfigCategoriesMapStruct<C> operators;
    public final ModalFunctionConfigsMapStruct<C> modalFunctions;
    public final PropertyModifierConfigsMapStruct propModifiers;

    public final IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry;
    public final OperatorTypeRegistry<C> operatorTypeRegistry;

    private final HashMap<Class<? extends UISpace>, Set<OperatorConfigCategoryGroupStruct<C>>> spaceMapping = new HashMap<>();
    private final HashMap<Class<? extends UIRegion>, Set<OperatorConfigCategoryGroupStruct<C>>> regionMapping = new HashMap<>();

    public UserFunctionalityConfigGroupStruct(String id, IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry,
                                              OperatorTypeRegistry<C> operatorTypeRegistry) {
        super(id);
        this.hotKeyConfigRegistry = hotKeyConfigRegistry;
        this.operatorTypeRegistry = operatorTypeRegistry;
        this.addChild("configs", this.operators = new OperatorConfigCategoriesMapStruct<>("key_configurations", this));
        this.addChild("modal_functions", this.modalFunctions = new ModalFunctionConfigsMapStruct<>("modal_functions", this.hotKeyConfigRegistry));
        this.addChild("prop_modifiers", this.propModifiers = new PropertyModifierConfigsMapStruct("prop_modifiers"));
        this.operatorTypeRegistry.getEventBus().subscribe(this);
        this.operatorTypeRegistry.getChildren().forEach(ot -> {
            if (ot instanceof IOTModalModifierProvider provider) this.addPropertyModifierConfig(provider);
        });
    }

    @Override
    protected final void onChildEvent(AbstractEvent event) {
        if (event instanceof AddToTreeEvent addToTreeEvent) {
            if (addToTreeEvent.getTrigger() instanceof OperatorConfigCategoryGroupStruct opCategory) {
                this.addSpaceRegionMapping(Set.of(opCategory));
            }
        } else if (event instanceof RemoveFromTreeEvent removeFromTreeEvent
                && removeFromTreeEvent.getTrigger() instanceof OperatorConfigCategoryGroupStruct opCategory) {
            this.removeSpaceRegionMapping(Set.of(opCategory));
        }
    }

    @SubscribeEvent(type = SubscribeEvent.Type.ON_NODE)
    private void onAddOT(ValueAddEvent event) {
        if (event.getAddedElement() instanceof IOTModalModifierProvider provider) {
            this.addPropertyModifierConfig(provider);
        }
    }

    private <P extends OperatorProperties> void addPropertyModifierConfig(IOTModalModifierProvider<C, ?, P> provider) {
        Set<PropertyModifiersListStruct<P>> modifiers = this.propModifiers.getConfigCategoriesForDirect(provider.getPropertyType());

        if (modifiers.isEmpty()) {
            this.propModifiers.add(new PropertyModifiersListStruct<>(provider.modifierConfigIdentifier(),
                    provider.getPropertyType(), provider.modifierRegistry(), this.hotKeyConfigRegistry));
        }
    }

    /**
     * @param space
     * @param region
     * @return all OperatorConfigs that were registered to a superclass or the same class of the provided space and region ui class.
     * Superclass so Operators can be registered to one class and be reused for many special ui subclasses, utilizing interface segregation principle.
     * Order is determined by region classes, where the first element is the equal class and the last element is the highest super class.
     */
    public LinkedHashSet<OperatorConfigGroupStruct<C>> getConfigsForUISpaceRegion(Class<? extends UISpace> space, Class<? extends UIRegion> region) {
        //TODO we need an efficient datastructure with minimum retrieval time for class inheritance e.g. get(Class) returns the class and all superclasses or subclasses for events
        List<Pair<Class<? extends UIRegion>, Set<OperatorConfigGroupStruct<C>>>> list = new ArrayList<>();
        /*
         * Go through the regiontypes sorting them to their class hierarchy.
         * Do not include null region or spaces.
         */
        for (Map.Entry<Class<? extends UIRegion>, Set<OperatorConfigCategoryGroupStruct<C>>> entry : this.regionMapping.entrySet()) {
            Class<? extends UIRegion> regionType = entry.getKey();
            if (!(regionType != null && regionType.isAssignableFrom(region))) continue;

            /*
             * Go through the categories for the same or superclass of the region.
             */
            for (OperatorConfigCategoryGroupStruct<C> category : entry.getValue()) {
                if (!(category.getSpacetype() != null && category.getSpacetype().isAssignableFrom(space))) continue;

                Pair<Class<? extends UIRegion>, Set<OperatorConfigGroupStruct<C>>> p = new Pair<>(regionType, Set.copyOf(category.configs.getChildren()));
                if (list.isEmpty()) {
                    list.add(p);
                } else {
                    /*
                     * insert the current region before the first superclass found, shifting the elements.
                     */
                    for (int i = 0; i < list.size(); i++) {
                        Class<? extends UIRegion> sortedRegionType = list.get(i).getA();
                        if (sortedRegionType.isAssignableFrom(regionType)) {
                            list.add(i, p);
                            break;
                        } else if (i == list.size() - 1) {
                            /* if it is not a subclass of the last element, just append it. */
                            list.add(i, p);
                        }
                    }
                }
            }
        }

        LinkedHashSet<OperatorConfigGroupStruct<C>> result = new LinkedHashSet<>();
        for (int i = 0; i < list.size(); i++) {
            result.addAll(list.get(i).getB());
        }
        return result;
    }

    /**
     *
     * @param space
     * @return all configurations that are registered to a space without a specific region.
     */
    public LinkedHashSet<OperatorConfigGroupStruct<C>> getGlobalConfigsForUISpace(Class<? extends UISpace> space) {
        List<Pair<Class<? extends UISpace>, Set<OperatorConfigGroupStruct<C>>>> list = new ArrayList<>();
        /*
         * Go through the spacetypes sorting them to their class hierarchy.
         * Do not include null region or spaces.
         */
        for (Map.Entry<Class<? extends UISpace>, Set<OperatorConfigCategoryGroupStruct<C>>> entry : this.spaceMapping.entrySet()) {
            Class<? extends UISpace> spaceType = entry.getKey();
            if (!(spaceType != null && spaceType.isAssignableFrom(space))) continue;

            /*
             * Go through the categories and remove all that have a region type set.
             * Sort according to space class hierarchy.
             */
            for (OperatorConfigCategoryGroupStruct<C> category : entry.getValue()) {
                if (category.getRegiontype() != null) continue;

                Pair<Class<? extends UISpace>, Set<OperatorConfigGroupStruct<C>>> p = new Pair<>(spaceType, Set.copyOf(category.configs.getChildren()));
                if (list.isEmpty()) {
                    list.add(p);
                } else {
                    /*
                     * insert the current region before the first superclass found, shifting the elements.
                     */
                    for (int i = 0; i < list.size(); i++) {
                        Class<? extends UISpace> sortedSpaceType = list.get(i).getA();
                        if (sortedSpaceType.isAssignableFrom(spaceType)) {
                            list.add(i, p);
                            break;
                        } else if (i == list.size() - 1) {
                            /* if it is not a subclass of the last element, just append it. */
                            list.add(i, p);
                        }
                    }
                }
            }
        }

        LinkedHashSet<OperatorConfigGroupStruct<C>> result = new LinkedHashSet<>();
        for (int i = 0; i < list.size(); i++) {
            result.addAll(list.get(i).getB());
        }
        return result;
    }

    /**
     * @return all configurations that are registered to no space or region, making them global.
     */
    public Set<OperatorConfigGroupStruct<C>> getGlobalConfigs() {
        Set<OperatorConfigGroupStruct<C>> result = new HashSet<>();

        /* use spaces as there will probably be less keys */
        Set<OperatorConfigCategoryGroupStruct<C>> spaceCategories;
        if ((spaceCategories = this.spaceMapping.get(null)) != null) {
            /* go through the null spaces and only add where region is null too */
            for (OperatorConfigCategoryGroupStruct<C> category : spaceCategories) {
                if (category.getRegiontype() != null) continue;
                result.addAll(category.configs.getChildren());
            }
        }

        return result;
    }

    /**
     *
     * @param region
     * @return all configurations that are registered to a region without a specific space, making them
     * reusable across different spaces.
     */
    public LinkedHashSet<OperatorConfigGroupStruct<C>> getGlobalConfigsForRegion(Class<? extends UIRegion> region) {
        List<Pair<Class<? extends UIRegion>, Set<OperatorConfigGroupStruct<C>>>> list = new ArrayList<>();
        /*
         * Go through the spacetypes sorting them to their class hierarchy.
         * Do not include null region or spaces.
         */
        for (Map.Entry<Class<? extends UIRegion>, Set<OperatorConfigCategoryGroupStruct<C>>> entry : this.regionMapping.entrySet()) {
            Class<? extends UIRegion> regionType = entry.getKey();
            if (!(regionType != null && regionType.isAssignableFrom(region))) continue;

            /*
             * Go through the categories and remove all that have a space type set.
             * Sort according to space class hierarchy.
             */
            for (OperatorConfigCategoryGroupStruct<C> category : entry.getValue()) {
                if (category.getSpacetype() != null) continue;

                Pair<Class<? extends UIRegion>, Set<OperatorConfigGroupStruct<C>>> p = new Pair<>(regionType, Set.copyOf(category.configs.getChildren()));
                if (list.isEmpty()) {
                    list.add(p);
                } else {
                    /*
                     * insert the current region before the first superclass found, shifting the elements.
                     */
                    for (int i = 0; i < list.size(); i++) {
                        Class<? extends UIRegion> sortedRegionType = list.get(i).getA();
                        if (sortedRegionType.isAssignableFrom(regionType)) {
                            list.add(i, p);
                            break;
                        } else if (i == list.size() - 1) {
                            /* if it is not a subclass of the last element, just append it. */
                            list.add(i, p);
                        }
                    }
                }
            }
        }

        LinkedHashSet<OperatorConfigGroupStruct<C>> result = new LinkedHashSet<>();
        for (int i = 0; i < list.size(); i++) {
            result.addAll(list.get(i).getB());
        }
        return result;
    }

    private void removeSpaceRegionMapping(Set<OperatorConfigCategoryGroupStruct<C>> categories) {
        for (OperatorConfigCategoryGroupStruct<C> opCategory : categories) {
            Set<OperatorConfigCategoryGroupStruct<C>> spaceMappingSet;
            Set<OperatorConfigCategoryGroupStruct<C>> regionMappingSet;
            if ((spaceMappingSet = this.spaceMapping.get(opCategory.getSpacetype())) != null) {
                spaceMappingSet.remove(opCategory);
            }

            if ((regionMappingSet = this.regionMapping.get(opCategory.getRegiontype())) != null) {
                regionMappingSet.remove(opCategory);
            }

            this.removeSpaceRegionMapping(opCategory.subcategories.getChildren());
        }
    }

    private void addSpaceRegionMapping(Set<OperatorConfigCategoryGroupStruct<C>> categories) {
        for (OperatorConfigCategoryGroupStruct<C> opCategory : categories) {
            Set<OperatorConfigCategoryGroupStruct<C>> spaceMappingSet;
            Set<OperatorConfigCategoryGroupStruct<C>> regionMappingSet;
            Class<? extends UISpace> spaceType = opCategory.getSpacetype();
            Class<? extends UIRegion> regionType = opCategory.getRegiontype();
            if ((spaceMappingSet = this.spaceMapping.get(spaceType)) == null) {
                this.spaceMapping.put(spaceType, spaceMappingSet = new HashSet<>());
            }

            if ((regionMappingSet = this.regionMapping.get(regionType)) == null) {
                this.regionMapping.put(regionType, regionMappingSet = new HashSet<>());
            }

            spaceMappingSet.add(opCategory);
            regionMappingSet.add(opCategory);
            this.addSpaceRegionMapping(opCategory.subcategories.getChildren());
        }
    }
}