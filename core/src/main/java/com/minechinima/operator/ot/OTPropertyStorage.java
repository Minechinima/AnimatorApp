package com.minechinima.operator.ot;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseValueList;
import com.minechinima.model.values.base.BaseValueOrderedCollection;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Utility class that has common attributes and logic that are needed by an {@link OperatorType} which provides {@link OperatorPropertiesGroupStruct}.
 * @param <P>
 */
public class OTPropertyStorage<P extends OperatorPropertiesGroupStruct> extends ValueGroup {
    /**
     * Runtime properties cached here so they can be undone and adressed properly.
     * TODO how to prevent these from being stored to harddrive? Not serializing sounds a bit too early in the process.
     */
    private final BaseValueOrderedCollection<P> runtimeProps = new BaseValueList<>("runtime_props") {
        @Override
        protected @NotNull P instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
            P props = OTPropertyStorage.this.createProps();
            props.fromData(map);
            return props;
        }
    };
    private final Supplier<P> createProps;

    public OTPropertyStorage(String id, Supplier<P> createProps) {
        super(id);
        this.addChild("runtime_props", this.runtimeProps);
        this.createProps = createProps;
    }

    public P createProps() {
        return this.createProps.get();
    }

    public final void addRuntimeProps(P props) {
        if (!props.isInitialized()) props.initChildren();
        this.runtimeProps.add(props);
    }

    public final void removeRuntimeProps(P props) {
        this.runtimeProps.remove(props);
    }
}
