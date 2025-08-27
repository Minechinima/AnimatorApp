package com.minechinima.operator.history;

import com.minechinima.model.logging.operations.IDataOperation;

/**
 * Registry for strategies how to handle {@link com.minechinima.model.logging.operations.IDataOperation} in the log
 * for compression.
 */
public class CompressionStrategyRegistry {
    public <T extends IDataOperation, J extends IDataOperation> void registerMerge(
            Class<T> op0, Class<J> op1, MergeStrategy<T, J> strategy) {

    }

    /**
     *
     * @param op0
     * @param op1
     * @param strategy
     * @param <T>
     * @param <J>
     */
    public <T extends IDataOperation, J extends IDataOperation> void registerParallelCheck(
            Class<T> op0, Class<J> op1, ParallelCheckStrategy<T, J> strategy) {

    }

    /**
     * The order of the classes does not matter.
     * @param op0
     * @param op1
     * @return
     * @param <T>
     * @param <J>
     */
    public <T extends IDataOperation, J extends IDataOperation> MergeStrategy<T, J> getMergeStrategy(Class<T> op0, Class<J> op1) {
        return null;
    }

    /**
     * The order of the classes does not matter.
     * @param op0
     * @param op1
     * @return
     * @param <T>
     * @param <J>
     */
    public <T extends IDataOperation, J extends IDataOperation> ParallelCheckStrategy<T, J> getParallelCheckStrategy(Class<T> op0, Class<J> op1) {
        return null;
    }
}
