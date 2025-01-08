package com.minechinima.operator;

import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.AbstractOperatorType;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class OperatorFactory {
    private final Map<AbstractOperatorType<?, ?>, PropertyOperatorEntry<?, ?, ?, ?>> propRegistry = new HashMap<>();
    private final Map<AbstractOperatorType<?, ?>, Supplier<IOperator<?>>> opRegistry = new HashMap<>();
    private final Logger logger;

    public OperatorFactory(Logger logger) {
        this.logger = logger;
    }

    @Nullable
    public <C, OP extends IOperator<C>,
            OT extends AbstractOperatorType<C, ?>>
    OP getOperator(OT type) {
        Supplier<OP> supplier;
        PropertyOperatorEntry<C, ?, ?, ?> prop;

        try {
            if ((supplier = (Supplier<OP>) this.opRegistry.get(type)) != null) {
                return supplier.get();
            }

            if (type instanceof AbstractOperatorType.PropertyProvider propOT
                    && (prop= (PropertyOperatorEntry) this.propRegistry.get(type)) != null) {
                /* cast exception theoretically impossible because registering process takes care of type safety... */
                Function f = prop.init;
                return (OP) f.apply(propOT.createProps());
            }
        } catch (ClassCastException e) {
            //TODO I am not sure if class cast is possible...
            return null;
        }

        return null;
    }

    @Nullable
    public <C, P extends OperatorProperties,
            OP extends IOperator<C> & IPropertyOperator<C, P>,
            OT extends AbstractOperatorType<C, ?> & AbstractOperatorType.PropertyProvider<P>>
    OP getOperator(OT type, @NotNull P props) {
        PropertyOperatorEntry<C, P, OT, OP> entry;
        try {
            if ((entry = (PropertyOperatorEntry<C, P, OT, OP>) this.propRegistry.get(type)) == null) return null;
        } catch (ClassCastException e) {
            return null;
        }

        return entry.init.apply(props);
    }

    public <C, P extends OperatorProperties,
            OP extends IOperator<C> & IPropertyOperator<C, P>,
            OT extends AbstractOperatorType<C, ? extends AbstractOperatorType<C, ?>> & AbstractOperatorType.PropertyProvider<P>>
    void register(OT type, Function<P, OP> init) {
        PropertyOperatorEntry<C, P, OT, OP> entry = new PropertyOperatorEntry<>(type, init);
        this.replacement(type, this.opRegistry);
        this.propRegistry.put(type, entry);
    }

    public <C, OP extends IOperator<C>,
            OT extends AbstractOperatorType<C, ? extends AbstractOperatorType<C, ?>>>
    void register(OT type, Supplier<OP> init) {
        this.replacement(type, this.propRegistry);
        this.opRegistry.put(type, (Supplier<IOperator<?>>) init);
    }

    private void replacement(AbstractOperatorType type, Map other) {
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
    private class PropertyOperatorEntry<C, P extends OperatorProperties,
            OT extends AbstractOperatorType<C, ? extends AbstractOperatorType<C, ?>> & AbstractOperatorType.PropertyProvider<P>,
            OP extends IOperator<C> & IPropertyOperator<C, P>> {
        private final OT type;
        private final Function<P, OP> init;

        private PropertyOperatorEntry(OT type, Function<P, OP> init) {
            this.type = type;
            this.init = init;
        }
    }
}
