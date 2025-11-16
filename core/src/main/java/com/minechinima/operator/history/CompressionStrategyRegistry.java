package com.minechinima.operator.history;

import com.minechinima.model.logging.operations.IDataOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Registry for strategies how to handle {@link com.minechinima.model.logging.operations.IDataOperation} in the log
 * for compression.
 */
public class CompressionStrategyRegistry {
    private final HashMap<MergeStrategyKey, MergeStrategy<?,?>> mergeStrategies = new HashMap<>();
    private final HashMap<ParallelCheckKey, ParallelCheckStrategy<?,?>> parallelCheckStrategies = new HashMap<>();

    /**
     * @param op0
     * @param op1
     * @param strategy
     * @param <T>
     * @param <J>
     */
    public <T extends IDataOperation, J extends IDataOperation> void registerMerge(
            Class<T> op0, Class<J> op1, MergeStrategy<T, J> strategy) {
        MergeStrategyKey key = new MergeStrategyKey(op0, op1);
        this.mergeStrategies.put(key, strategy);
    }

    /**
     * @param op0
     * @param op1
     * @param strategy
     * @param <T>
     * @param <J>
     */
    public <T extends IDataOperation, J extends IDataOperation> void registerParallelCheck(
            Class<T> op0, Class<J> op1, ParallelCheckStrategy<T, J> strategy) {
        ParallelCheckKey key = new ParallelCheckKey(op0, op1);
        this.parallelCheckStrategies.put(key, strategy);
    }

    /**
     * @param op0
     * @param op1
     * @return
     * @param <T>
     * @param <J>
     */
    public <T extends IDataOperation, J extends IDataOperation> MergeStrategy<T, J> getMergeStrategy(Class<T> op0, Class<J> op1) {
        return (MergeStrategy<T, J>) this.mergeStrategies.get(new MergeStrategyKey(op0, op1));
    }

    /**
     * @param op0
     * @param op1
     * @return
     */
    public boolean canBeParallel(IDataOperation op0, IDataOperation op1) {
        //TODO: search logic needs to take into account overriding rules for subclasses.
        for (Map.Entry<ParallelCheckKey, ParallelCheckStrategy<?,?>> entry : this.parallelCheckStrategies.entrySet()) {
            ParallelCheckKey key = entry.getKey();
            ParallelCheckStrategy strategy = entry.getValue();
            Class<? extends IDataOperation> op0Class = key.switched ? key.op1 : key.op0;
            Class<? extends IDataOperation> op1Class = key.switched ? key.op0 : key.op1;

            if (op0Class.isAssignableFrom(op0.getClass()) && op1Class.isAssignableFrom(op1.getClass())) {
                return strategy.canBeParallel(op0, op1);
            } else if (op1Class.isAssignableFrom(op0.getClass()) && op0Class.isAssignableFrom(op1.getClass())) {
                return strategy.canBeParallel(op1, op0);
            }
        }

        return false;
    }

    protected record MergeStrategyKey(Class<? extends IDataOperation> op0, Class<? extends IDataOperation> op1) {
    }

    //TODO testing if order of classes does not matter when registering and if retrieving works.
    protected class ParallelCheckKey {
        private final Class<? extends IDataOperation> op0;
        private final Class<? extends IDataOperation> op1;
        private final boolean switched;

        protected ParallelCheckKey(Class<? extends IDataOperation> op0, Class<? extends IDataOperation> op1) {
            if (Objects.hash(op0) > Objects.hash(op1)) {
                this.op0 = op1;
                this.op1 = op0;
                this.switched = true;
            } else {
                this.op0 = op0;
                this.op1 = op1;
                this.switched = false;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ParallelCheckKey that)) return false;
            return Objects.equals(op0, that.op0) && Objects.equals(op1, that.op1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(op0, op1);
        }
    }
}
