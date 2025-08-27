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
    boolean canBeParallel(T a, J b);
}
