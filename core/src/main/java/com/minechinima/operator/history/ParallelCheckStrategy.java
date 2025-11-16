package com.minechinima.operator.history;

import com.minechinima.model.logging.operations.IDataOperation;

/**
 * This strategy is for checking if two operations could be run simultaneously, meaning they do not
 * interfere with eachother. This can be used to merge more operations across different non mergeable operations that are parallel.
 * <br><br>
 * Classes passed to this can be subclasses.
 *
 * @param <T>
 * @param <J>
 */
@FunctionalInterface
public interface ParallelCheckStrategy<T extends IDataOperation, J extends IDataOperation> {
    /**
     *
     * @param a
     * @param b
     * @return true if the two operations could be run in parallel and do not interfere with each other.
     */
    boolean canBeParallel(T a, J b);
}
