package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.ComponentTypes;
import com.minechinima.depsgraph.DataScope;
import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DepsGraphBuilderRegistry {
    private static final GenericComponentBuilder<BaseStructure> GENERICDATAENTRY = new GenericComponentBuilder<>(ComponentTypes.DATA_ENTRY) {
        @Override
        public @Nullable DataScope getDataScope(BaseStructure data) {
            return null;
        }
    };
    private static final GenericComponentBuilder<BaseStructure> GENERICDATAEXIT = new GenericComponentBuilder<>(ComponentTypes.DATA_EXIT) {
        @Override
        public @Nullable DataScope getDataScope(BaseStructure data) {
            return null;
        }
    };

    private final HashMap<Class<? extends BaseStructure>, BuilderEntry<? extends BaseStructure>> builder = new HashMap<>();

    public <T extends BaseStructure> void registerBuilder(Class<T> dataClass, ComponentBuilder<? super T> entryBuilder, ComponentBuilder<? super T> exitBuilder) {
        if (this.builder.containsKey(dataClass)) {
            throw new IllegalArgumentException("Builder for data class " + dataClass.getName() + " already registered.");
        }
        this.builder.put(dataClass, new BuilderEntry<>(entryBuilder, exitBuilder));
    }

    public <T extends BaseStructure> void registerBuilder(Class<T> dataClass) {
        if (this.builder.containsKey(dataClass)) {
            throw new IllegalArgumentException("Builder for data class " + dataClass.getName() + " already registered.");
        }
        this.builder.put(dataClass, new BuilderEntry<>(GENERICDATAENTRY, GENERICDATAEXIT));
    }

    public <T extends BaseStructure> void registerBuilder(Class<T> dataClass, ComponentBuilder<? super T> builder) {
        if (!this.builder.containsKey(dataClass)) {
            throw new IllegalArgumentException("Data class needs to be registered first.");
        }
        ((BuilderEntry<T>) this.builder.get(dataClass)).builders.add(builder);
    }

    protected record BuilderEntry<T extends BaseStructure>(ComponentBuilder<? super T> entryBuilder,
                                                           ComponentBuilder<? super T> exitBuilder,
                                                           Set<ComponentBuilder<? super T>> builders) {
        public BuilderEntry(ComponentBuilder<? super T> entryBuilder, ComponentBuilder<? super T> exitBuilder) {
            this(entryBuilder, exitBuilder, new HashSet<>());
        }
    }
}
