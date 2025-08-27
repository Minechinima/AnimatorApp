package com.minechinima.operator.history;

import com.minechinima.controller.Transaction;
import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.OperatorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class HistoryManager {
    protected Stack<HistoryStep> undos = new Stack<>();
    protected Stack<HistoryStep> redos = new Stack<>();
    protected final CompressionStrategyRegistry compressionStrategyRegistry;

    public HistoryManager(CompressionStrategyRegistry compressionStrategyRegistry) {
        this.compressionStrategyRegistry = compressionStrategyRegistry;
    }

    protected Stack<DataOperationLog.Entry> compressLog(Stack<DataOperationLog.Entry> log) {
        Stack<DataOperationLog.Entry> newLog = new Stack<>();
        /*
         * Store operations in their order that could be executed simultaneously.
         * Merge next operations onto the parallel ones.
         */
        List<DataOperationLog.Entry> parallel = new LinkedList<>();

        /*
         * Go through the log and cache all entries, where the operations can be executed parallel until
         * one is found that is not parallel to at least one of the cached entries.
         * Try to merge next operations onto the cached parallel operations.
         */
        for (int i = 0; i < log.size(); i++) {
            DataOperationLog.Entry nextEntry = log.get(i);
            boolean merged = false;
            boolean notParallel = false;
            for (DataOperationLog.Entry cachedParallelEntry : parallel) {
                ParallelCheckStrategy parallelStrategy = this.compressionStrategyRegistry.getParallelCheckStrategy(
                        cachedParallelEntry.getOperation().getClass(), nextEntry.getOperation().getClass());
                if (!parallelStrategy.canBeParallel(cachedParallelEntry.getOperation(), nextEntry.getOperation())) {
                    notParallel = true;
                }

                MergeStrategy mergeStrategy = this.compressionStrategyRegistry.getMergeStrategy(cachedParallelEntry.getOperation().getClass(), nextEntry.getOperation().getClass());
                IDataOperation mergedOp = null;
                /* merge strategy returns same type as current,
                 * assume that is still parallel to the cache, otherwise another for loop is required
                 * TODO review
                 */
                if (mergeStrategy != null && (mergedOp = mergeStrategy.merge(cachedParallelEntry.getOperation(), nextEntry.getOperation())) != null) {
                    nextEntry = new HistoryLogEntry(cachedParallelEntry.getTimetamp(), mergedOp, cachedParallelEntry.getPath());
                    notParallel = false;
                    merged = true;
                    break;
                }
            }

            if (notParallel) {
                newLog.addAll(parallel);
                newLog.push(nextEntry);
                parallel.clear();
            } else if (!merged) {
                parallel.add(nextEntry);
            }
        }

        newLog.addAll(parallel);

        return newLog;
    }

    public void pushUndo(Transaction transaction, OperatorType type, @Nullable OperatorPropertiesGroupStruct props) {
        if (type instanceof IOTPropertyProvider<?> && props == null) {
            throw new IllegalArgumentException("Provided a IOTPropertyProvider without OperatorProperties.");
        }

        if (transaction.getState() != Transaction.State.COMMITTED) {
            throw new IllegalArgumentException("Transaction is not commited, cannot be pushed as undo step.");
        }

        this.undos.push(new HistoryStep(transaction.getLocalizedTitle(), type, props,
                this.compressLog(transaction.getDataOperationStack())));
    }

    public void pushUndo(Transaction transaction, UndoRedoStep step) {
        if (step.isUndo) {
            throw new IllegalArgumentException("Cannot push a step from undo stack into the undo stack again.");
        }

        this.undos.push(new HistoryStep(step.title, step.ot, step.props,
                this.compressLog(transaction.getDataOperationStack())));
    }

    public void pushRedo(Transaction transaction, UndoRedoStep step) {
        if (transaction.getState() != Transaction.State.COMMITTED) {
            throw new IllegalArgumentException("Transaction is not commited, cannot be pushed as redo step.");
        }

        if (!step.isUndo) {
            throw new IllegalArgumentException("Cannot push a step from redo stack into the redo stack again.");
        }

        this.redos.push(new HistoryStep(step.title, step.ot, step.props,
                this.compressLog(transaction.getDataOperationStack())));
    }

    /**
     * Pops the last undo step and creates a controller command for undoing every in the provided data node and its children.
     * @param relative
     * @return
     */
    @Nullable
    public HistoryManager.UndoRedoStep popUndo(BaseValueHierarchy relative) {
        if (this.undos.isEmpty()) return null;
        HistoryStep step = this.undos.pop();

        return new UndoRedoStep(step.stepTitle, this.stepCommand(relative, step), step.ot, step.props, true);
    }

    @Nullable
    public HistoryManager.UndoRedoStep popRedo(BaseValueHierarchy relative) {
        if (this.redos.isEmpty()) return null;
        HistoryStep step = this.redos.pop();

        return new UndoRedoStep(step.stepTitle, this.stepCommand(relative, step), step.ot, step.props, false);
    }

    protected IControllerCommand stepCommand(BaseValueHierarchy relative, HistoryStep step) {
        return new IControllerCommand() {
            @Override
            public void execute() throws UserException {
                Stack<DataOperationLog.RelativeEntry> stack = DataOperationLog.getStackRelativeTo(relative, step.log);
                while (!stack.isEmpty()) {
                    DataOperationLog.RelativeEntry entry = stack.pop();

                    BaseStructure node;
                    if (entry.getRelativePath() == null) {
                        node = relative;
                    } else {
                        node = relative.getChildByUniquePath(entry.getRelativePath());
                    }

                    if (node == null) throw new UserException("Could not undo as the data node was not found.");

                    IDataOperation op = entry.getOperation();

                    if (!op.isCorrectType(node) || !op.undo(node)) {
                        throw new UserException("Could not undo as one step failed.");
                    }
                }
            }

            @Override
            public List<BaseStructure> getAffectedNodes() {
                List<BaseStructure> nodes = new LinkedList<>();
                Stack<DataOperationLog.RelativeEntry> stack = DataOperationLog.getStackRelativeTo(relative, step.log);
                while (stack.isEmpty()) {
                    DataOperationLog.RelativeEntry entry = stack.pop();

                    BaseStructure node;
                    if (entry.getRelativePath() == null) {
                        nodes.add(relative.getRoot());
                    } else {
                        nodes.add(relative.getChildByUniquePath(entry.getRelativePath()));
                    }
                }
                return nodes;
            }
        };
    }

    protected record HistoryStep(String stepTitle, OperatorType ot, @Nullable OperatorPropertiesGroupStruct props, Stack<DataOperationLog.Entry> log) { }

    public final static class UndoRedoStep {
        public final IControllerCommand command;
        public final OperatorType ot;
        @Nullable
        public final OperatorPropertiesGroupStruct props;
        public final String title;
        /**
         * For the HistoryManager to verify that when pushing into redo stack the step is from undo stack.
         */
        private final boolean isUndo;

        /**
         * Private so only history manager can instantiate. This ensures that only an undo step created by this history can be inserted
         * as redo step.
         */
        private UndoRedoStep(String title, IControllerCommand command, OperatorType ot, @Nullable OperatorPropertiesGroupStruct props, boolean isUndo) {
            this.ot = ot;
            this.command = command;
            this.props = props;
            this.title = title;
            this.isUndo = isUndo;
        }
    }

    protected static class HistoryLogEntry extends DataOperationLog.Entry {
        protected HistoryLogEntry(long timestamp, IDataOperation op, ValuePath path) {
            super(timestamp, path, op);
        }
    }
}