package com.minechinima.operator;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class OperatorFactory<C extends OperatorContext> {
    private Map<OperatorType<C, ?>, OperatorInit<?>> registry = new HashMap<>();

    public boolean hasType(OperatorType<C, ?> type) {
        return this.registry.containsKey(type);
    }

    public <P extends OperatorProperties> void register(OperatorType<C, P> type, Function<P, Operator<P, C>> init) {
        this.registry.put(type, new OperatorInit<>(type, init));
    }

    @Nullable
    public <P extends OperatorProperties> Operator<P, C> createOperator(OperatorType<C, P> type, P props) {
        if (!this.registry.containsKey(type)) return null;

        OperatorInit<P> op = (OperatorInit<P>) this.registry.get(type);

        return op.init.apply(props);
    }

    @Nullable
    public <P extends OperatorProperties> Operator<P, C> createOperator(OperatorType<C, P> type) {
        if (!this.registry.containsKey(type)) return null;

        OperatorInit<P> op = (OperatorInit<P>) this.registry.get(type);

        return op.init.apply(op.type.createProps());
    }

    private class OperatorInit<P extends OperatorProperties> {
        private final OperatorType<C, P> type;
        private final Function<P, Operator<P, C>> init;

        private OperatorInit(OperatorType<C, P> type, Function<P, Operator<P, C>> init) {
            this.type = type;
            this.init = init;
        }
    }
}
