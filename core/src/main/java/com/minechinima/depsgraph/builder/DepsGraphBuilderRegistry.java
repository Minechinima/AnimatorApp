package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.ComponentNode;
import com.minechinima.depsgraph.ComponentTypes;
import com.minechinima.depsgraph.DataNode;
import com.minechinima.depsgraph.DataScope;
import com.minechinima.model.api.IData;
import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DepsGraphBuilderRegistry {
    private static final GenericComponentBuilder<IData, IData, IData> GENERICDATAENTRY = new GenericComponentBuilder<>(ComponentTypes.DATA_ENTRY) {
        @Override
        public @Nullable DataScope getDataScope(IData data) {
            return null;
        }
    };
    private static final GenericComponentBuilder<IData, IData, IData> GENERICDATAEXIT = new GenericComponentBuilder<>(ComponentTypes.DATA_EXIT) {
        @Override
        public @Nullable DataScope getDataScope(IData data) {
            return null;
        }
    };

    //TODO type safety for eval datatype not ensured.

    private final HashMap<Class<? extends BaseStructure>, BuilderEntry> builder = new HashMap<>();
    /**
     * Map builders to the types, to allow registering builders without needing access to the builder instance.
     * Also forces uniqueness and transparency of what each component key builds.
     */
    private final HashMap<ComponentNode.ComponentType, BuilderTypeEntry<?, ?>> builderTypes = new HashMap<>();

    public DepsGraphBuilderRegistry() {
        this.registerComponentBuilderType(GENERICDATAENTRY, IData.class);
        this.registerComponentBuilderType(GENERICDATAEXIT, IData.class);
    }

    /**
     * Register a builder for a component.
     * @param builder
     * @param clazz The data class this component builder can build for.
     * @param <T>
     */
    public <DATA extends T, T extends IData> void registerComponentBuilderType(ComponentBuilder<DATA, ?, T> builder, Class<DATA> clazz) {
        if (this.builderTypes.containsKey(builder.getType())) {
            throw new IllegalArgumentException("Builder for component type " + builder.getType() + " already registered.");
        }
        this.builderTypes.put(builder.getType(), new BuilderTypeEntry<>(builder, clazz));
    }

    public <T extends BaseStructure> void registerDataClass(Class<T> dataClass,
                                                          ComponentNode.ComponentType entryBuilderType,
                                                          ComponentNode.ComponentType exitBuilderType) {
        if (this.builder.containsKey(dataClass)) {
            throw new IllegalArgumentException("Data class " + dataClass.getName() + " already registered.");
        }

        this.throwExceptionsForWrongBuilderType(entryBuilderType, dataClass);
        this.throwExceptionsForWrongBuilderType(exitBuilderType, dataClass);

        this.builder.put(dataClass, new BuilderEntry(entryBuilderType, exitBuilderType));
    }

    public <T extends BaseStructure> BuilderRegistryHelper<T> registerDataClass(Class<T> dataClass) {
        if (this.builder.containsKey(dataClass)) {
            throw new IllegalArgumentException("Data class " + dataClass.getName() + " already registered.");
        }
        this.builder.put(dataClass, new BuilderEntry(ComponentTypes.DATA_ENTRY, ComponentTypes.DATA_EXIT));

        return new BuilderRegistryHelper<>(dataClass);
    }

    public <T extends BaseStructure> void registerBuilder(Class<T> dataClass, ComponentNode.ComponentType... builder) {
        if (builder.length == 0) {
            return;
        }

        for (ComponentNode.ComponentType type : builder) {
            this.registerBuilder(dataClass, type);
        }
    }

    public <T extends BaseStructure> void registerBuilder(Class<T> dataClass, ComponentNode.ComponentType builder) {
        if (!this.builder.containsKey(dataClass)) {
            throw new IllegalArgumentException("Data class needs to be registered first.");
        }

        this.throwExceptionsForWrongBuilderType(builder, dataClass);

        this.builder.get(dataClass).builders.add(builder);
    }

    public boolean hasBuilderForType(ComponentNode.ComponentType builder) {
        return this.builderTypes.containsKey(builder);
    }

    public boolean isBuilderTypeAssignable(ComponentNode.ComponentType builder, Class<? extends BaseStructure> dataClass) {
        if (!this.hasBuilderForType(builder)) {
            return false;
        }
        return this.builderTypes.get(builder).clazz.isAssignableFrom(dataClass);
    }

    private void throwExceptionsForWrongBuilderType(ComponentNode.ComponentType builder, Class<? extends BaseStructure> dataClass) {
        BuilderTypeEntry<?, ?> entry = this.builderTypes.get(builder);
        if (entry == null) {
            throw new IllegalArgumentException("Builder type " + builder + " not registered.");
        }
        if (entry.clazz.isAssignableFrom(dataClass)) {
            throw new IllegalArgumentException("Builder type " + builder + " not assignable to data class " + dataClass.getName() + ".");
        }
    }



    // TODO for building
    // component nodes that are built should have relation to entry and exit component



    protected record BuilderEntry(ComponentNode.ComponentType entryBuilder,
                                  ComponentNode.ComponentType exitBuilder,
                                  Set<ComponentNode.ComponentType> builders) {
        public BuilderEntry(ComponentNode.ComponentType entryBuilder, ComponentNode.ComponentType exitBuilder) {
            this(entryBuilder, exitBuilder, new HashSet<>());
        }
    }

    protected record BuilderTypeEntry<DATA extends T, T extends IData>(ComponentBuilder<DATA, ?, T> builder,
                                                               Class<?> clazz) {

    }

    public class BuilderRegistryHelper<T extends BaseStructure> {
        private final Class<T> registeredDataClass;

        protected BuilderRegistryHelper(Class<T> registeredDataClass) {
            this.registeredDataClass = registeredDataClass;
        }

        public BuilderRegistryHelper<T> registerBuilder(ComponentNode.ComponentType builder) {
            DepsGraphBuilderRegistry.this.registerBuilder(this.registeredDataClass, builder);
            return this;
        }

        public BuilderRegistryHelper<T> registerBuilders(ComponentNode.ComponentType... builder) {
            DepsGraphBuilderRegistry.this.registerBuilder(this.registeredDataClass, builder);
            return this;
        }
    }
}
