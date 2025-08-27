package com.minechinima.operator.history;

import com.minechinima.model.logging.operations.IDataOperation;
import org.jetbrains.annotations.Nullable;

/**
 * The merge strategy is only applied on the direct class types, subclasses will not be passed to the method.
 * <br><br>
 * Reasoning: Subclasses could have more internal attributes for their extended data manipulating behavior.
 * This behavior cannot be reflected by the merge strategy and the resulting merged operation as it is unaware
 * of these subclasses.
 *
 * @param <T>
 * @param <J>
 */
@FunctionalInterface
public interface MergeStrategy<T extends IDataOperation, J extends IDataOperation> {
    /**
     * Merge the current with the next operation.
     * The order of arguments influences the outcome.
     * @param current
     * @param next
     * @return null if the operations cannot be merged.
     */
    @Nullable
    T merge(T current, J next);
}
