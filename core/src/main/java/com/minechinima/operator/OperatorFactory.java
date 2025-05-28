package com.minechinima.operator;

import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

//TODO create base operatorTypRegistry, give it to OperatorFactory
public class OperatorFactory {
    private final Map<OperatorType<?>, PropertyOperatorEntry<?, ?, ?, ?>> propRegistry = new HashMap<>();
    private final Map<OperatorType<?>, Supplier<IOperator<?>>> opRegistry = new HashMap<>();
    private final Logger logger;

    public OperatorFactory(Logger logger) {
        this.logger = logger;
    }

    @Nullable
    public <C extends IContext, OP extends IOperator<C>,
            OT extends OperatorType<C>>
    OP getOperator(OT type) {
        Supplier<OP> supplier;
        PropertyOperatorEntry<C, ?, ?, ?> prop;

        try {
            if ((supplier = (Supplier<OP>) this.opRegistry.get(type)) != null) {
                return supplier.get();
            }

            if (type instanceof IOTPropertyProvider propOT
                    && (prop= (PropertyOperatorEntry) this.propRegistry.get(type)) != null) {
                /* cast exception theoretically impossible because registering process takes care of type safety... */
                Function f = prop.init;
                return (OP) f.apply(propOT.createProps());
            }
        } catch (ClassCastException e) {
            //TODO not sure if ClassCastException is even possible with the safety measures in the registration...
            return null;
        }

        return null;
    }

    @Nullable
    public <C extends IContext,
            P extends OperatorPropertiesGroupStruct,
            OP extends IPropertyOperator<C, P> & IOperator<C>,
            OT extends OperatorType<C> & IOTPropertyProvider<P>>
    OP getOperator(OT type, @NotNull P props) {
        PropertyOperatorEntry<C, P, OT, OP> entry;
        try {
            if ((entry = (PropertyOperatorEntry<C, P, OT, OP>) this.propRegistry.get(type)) == null) return null;
        } catch (ClassCastException e) {
            return null;
        }

        return entry.init.apply(props);
    }

    public <C extends IContext,
            P extends OperatorPropertiesGroupStruct,
            OP extends IPropertyOperator<C, P> & IOperator<C>,
            OT extends OperatorType<C> & IOTPropertyProvider<P>>
    void register(OT type, Function<P, OP> init) {
        PropertyOperatorEntry<C, P, OT, OP> entry = new PropertyOperatorEntry<>(type, init);
        this.replacement(type, this.opRegistry);
        this.propRegistry.put(type, entry);
    }

    public <C extends IContext,
            OP extends IOperator<C>,
            OT extends OperatorType<C>>
    void register(OT type, Supplier<OP> init) {
        this.replacement(type, this.propRegistry);
        this.opRegistry.put(type, (Supplier<IOperator<?>>) init);
    }

    private void replacement(OperatorType type, Map other) {
        if (this.opRegistry.containsKey(type) || this.propRegistry.containsKey(type)) {
            this.logger.info("Replacing the Operator of OperatorType[" + type.title + "] instance that was already registered.");
            /* only one operator type instance in this whole registry */
            other.remove(type);
        }
    }

    /**
     * Two entries are equal when {@link #type} is equal.
     * @param <C> context type
     * @param <P> properties
     * @param <OT> operator type
     * @param <OP> operator
     */
    private class PropertyOperatorEntry
            <C extends IContext,
            P extends OperatorPropertiesGroupStruct,
            OT extends OperatorType<C> & IOTPropertyProvider<P>,
            OP extends IPropertyOperator<C, P> & IOperator<C>> {
        private final OT type;
        private final Function<P, OP> init;

        private PropertyOperatorEntry(OT type, Function<P, OP> init) {
            this.type = type;
            this.init = init;
        }
    }
}
