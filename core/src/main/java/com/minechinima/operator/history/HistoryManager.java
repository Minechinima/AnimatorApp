package com.minechinima.operator.history;

import com.minechinima.controller.Transaction;
import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.operator.ot.commands.IUndoOperator;
import com.minechinima.utils.BooleanOperand;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public class HistoryManager {
    protected Stack<HistoryStep> undos = new Stack<>();
    protected Stack<HistoryStep> redos = new Stack<>();
    protected final CompressionStrategyRegistry compressionStrategyRegistry;

    public HistoryManager(CompressionStrategyRegistry compressionStrategyRegistry) {
        this.compressionStrategyRegistry = compressionStrategyRegistry;
    }

    public void pushUndo(OperatorType type,
                         IUndoOperator.IUndoOperatorCommand op,
                         @Nullable OperatorProperties props,
                         HistoryMarker... markers) {
        this.pushUndo(null, type, op, props, markers);
    }

    public void pushUndo(Transaction transaction,
                         OperatorType type, @Nullable
                         OperatorProperties props,
                         HistoryMarker... markers) {
        this.pushUndo(transaction, type, null, props, markers);
    }

    public void pushUndo(Transaction transaction,
                         HistoryMarker... markers) {
        this.pushUndo(transaction, null, null, markers);
    }

    public void pushUndo(@Nullable Transaction transaction,
                         OperatorType type,
                         IUndoOperator.IUndoOperatorCommand op,
                         @Nullable OperatorProperties props,
                         HistoryMarker... markers) {
        if (transaction == null && op == null) {
            throw new IllegalArgumentException("Transaction and undoOperator cannot be null at the same time.");
        }

        if (type instanceof IOTPropertyProvider<?, ?> && props == null) {
            throw new IllegalArgumentException("Provided a IOTPropertyProvider without OperatorProperties.");
        }

        if (transaction != null && transaction.getState() != Transaction.State.COMMITTED) {
            throw new IllegalArgumentException("Transaction is not commited, cannot be pushed as undo step.");
        }

        StepData data;
        String title;
        if (transaction != null) {
            data = new StepData(type, op, props, transaction.getLocalizedTitle(), this.compressLog(transaction.getDataOperationStack()));
            title = transaction.getLocalizedTitle();
        } else {
            data = new StepData(type, op, props, type.getIdentifier());
            title = type.title;
        }

        this.undos.push(new HistoryStep(title, data, markers));
    }




    public void pushUndo(Transaction transaction, UndoRedoStep redoStep) {
        this.pushRedoUndo(transaction, redoStep, true);
    }

    public void pushRedo(Transaction transaction, UndoRedoStep undoStep) {
        this.pushRedoUndo(transaction, undoStep, false);
    }

    private void pushRedoUndo(Transaction transaction, UndoRedoStep redoUndoStep, boolean pushToUndo) {
        if (transaction.getState() != Transaction.State.COMMITTED) {
            throw new IllegalArgumentException("Transaction is not commited, cannot be pushed as redo step.");
        }

        if (pushToUndo && redoUndoStep.isUndo) {
            throw new IllegalArgumentException("Cannot push a step from undo stack into the undo stack again.");
        } else if (!pushToUndo && !redoUndoStep.isUndo) {
            throw new IllegalArgumentException("Cannot push a step from redo stack into the redo stack again.");
        }

        Stack<StepData> steps = new Stack<>();
        Stack<DataOperationLog.Entry> compressedStepOperations = this.compressLog(transaction.getDataOperationStack());

        /*
         * if multiple steps were merged, a dummy redo/undo step needs to be created as there is no way
         * anymore to resolve the individual data operations from the undo/redo log to each redo step.
         */
        if (redoUndoStep.historyStep.steps.size() > 1) {
            /*
             * create the redo/undo steps in reverse order from the original undo/redo steps
             * (important to keep track of IUndoOperator for custom undo/redo on operator level)
             */
            for (StepData undoStepData : redoUndoStep.historyStep.steps) {
                if (undoStepData.ot == null) continue;
                steps.push(new StepData(undoStepData.ot, undoStepData.op, undoStepData.props, undoStepData.stepTitle));
            }
            /*
             * push the dummy redo/undo step at the top so it gets executed first
             * Important because we want the Operator.redo() method to work on the redone database
             */
            steps.push(new StepData(transaction.getLocalizedTitle(), compressedStepOperations));
        } else if (redoUndoStep.historyStep.steps.size() == 1) {
            StepData undoStepData = redoUndoStep.historyStep.steps.get(0);
            steps.push(new StepData(undoStepData.ot, undoStepData.op, undoStepData.props, undoStepData.stepTitle, compressedStepOperations));
        }

        if (pushToUndo) this.undos.push(new HistoryStep(redoUndoStep.historyStep.stepTitle, redoUndoStep.historyStep.markers, steps));
        else this.redos.push(new HistoryStep(redoUndoStep.historyStep.stepTitle, redoUndoStep.historyStep.markers, steps));
    }




    public void mergeUndoSteps(String stepTitle, HistoryMarker marker) {
        HistoryStep mergedStep = null;
        for (int i = this.undos.size() - 1; i >= 0; i--) {
            HistoryStep current = this.undos.get(i);
            if (current.markers.contains(marker)) {
                /* create a new history step with the current history step */
                if (mergedStep == null) {
                    mergedStep = new HistoryStep(stepTitle, current.markers, current.steps);
                    this.undos.remove(i);
                } else {
                    /* add all current history step to the bottom of the merged stack */
                    mergedStep.steps.addAll(0, current.steps);
                    mergedStep.markers.addAll(current.markers);
                    this.undos.remove(i);
                }
            } else if (mergedStep != null) {
                /* first step without the marker -> insert the merged step back into the history */
                this.undos.add(i + 1, mergedStep);
                mergedStep = null;
            }
        }
        /* means loop did not break -> it merged all to the start */
        if (mergedStep != null) {
            this.undos.add(0, mergedStep);
        }
    }




    /**
     * Pops the last undo step and creates a controller command for undoing every in the provided data node and its children.
     * @param relative what part of the tree should be changed.
     * @return
     */
    @Nullable
    public HistoryManager.UndoRedoStep popUndo(BaseValueHierarchy relative, IContext context, BiFunction<OperatorType, IContext, IContext> getContextForOT) {
        if (this.undos.isEmpty()) return null;
        HistoryStep step = this.undos.pop();

        return new UndoRedoStep(step, this.stepCommand(relative, step, context, getContextForOT, true), true);
    }

    /**
     * Create and pop a redo step to execute in the controller manager.
     * @param relative
     * @param context the current app context. Needed for undo operator commands
     * //TODO this probably needs to be the context the operator was executed in?
     *                do we even need to care, undo operator command should cache important variables, but how much should they cache - memory leak?
     * @param getContextForOT a function that gets the correct context type based on the operator type
     * @return
     */
    @Nullable
    public HistoryManager.UndoRedoStep popRedo(BaseValueHierarchy relative, IContext context,
                                               BiFunction<OperatorType, IContext, IContext> getContextForOT) {
        if (this.redos.isEmpty()) return null;
        HistoryStep step = this.redos.pop();

        return new UndoRedoStep(step, this.stepCommand(relative, step, context, getContextForOT, false), false);
    }



    /**
     * Pops only the first one consecutive array of undo steps that match the given history markers with the boolean operand.
     * It can pop anywhere in the history stack.
     * <br><br>
     * <b>This could fragment the history.</b>
     * @param relative
     * @param context
     * @param getContextForOT
     * @return
     */
    @Nullable
    public UndoRedoStep popConsecutiveUndoByMarker(BaseValueHierarchy relative,
                                                   IContext context,
                                                   BiFunction<OperatorType, IContext, IContext> getContextForOT,
                                                   HistoryMarker marker) {
        return this.popUndoByMarkerInternal(relative, context, getContextForOT, BooleanOperand.OR, false, marker);
    }

    /**
     * Pops only the first one consecutive array of undo steps that match the given history markers with the boolean operand.
     * It can pop anywhere in the history stack.
     * <br><br>
     * <b>This could fragment the history.</b>
     * @param relative
     * @param context
     * @param getContextForOT
     * @param mode
     * @param markers
     * @return
     */
    @Nullable
    public UndoRedoStep popConsecutiveUndoByMarker(BaseValueHierarchy relative,
                                           IContext context,
                                           BiFunction<OperatorType, IContext, IContext> getContextForOT,
                                           BooleanOperand mode,
                                           HistoryMarker... markers) {
        return this.popUndoByMarkerInternal(relative, context, getContextForOT, mode, false, markers);
    }

    /**
     * Pops all undo steps that match the given history markers with the boolean operand.
     * The popped undo steps can be non-consecutive and anywhere in the history stack.
     * <br><br>
     * <b>This could fragment the history.</b>
     * @return
     */
    @Nullable
    public UndoRedoStep popAllUndoByMarker(BaseValueHierarchy relative,
                                        IContext context,
                                        BiFunction<OperatorType, IContext, IContext> getContextForOT,
                                        BooleanOperand mode,
                                        HistoryMarker... markers) {
        return this.popUndoByMarkerInternal(relative, context, getContextForOT, mode, true, markers);
    }

    /**
     * Pops all undo steps that match the given history markers with the boolean operand.
     * The popped undo steps can be non-consecutive and anywhere in the history stack.
     * <br><br>
     * <b>This could fragment the history.</b>
     * @return
     */
    @Nullable
    public UndoRedoStep popAllUndoByMarker(BaseValueHierarchy relative,
                                           IContext context,
                                           BiFunction<OperatorType, IContext, IContext> getContextForOT,
                                           HistoryMarker marker) {
        return this.popUndoByMarkerInternal(relative, context, getContextForOT, BooleanOperand.OR, true, marker);
    }

    @Nullable
    private UndoRedoStep popUndoByMarkerInternal(BaseValueHierarchy relative, IContext context,
                                                 BiFunction<OperatorType, IContext, IContext> getContextForOT,
                                                 BooleanOperand mode, boolean popAll, HistoryMarker... markers) {
        if (this.undos.isEmpty() || markers == null || markers.length == 0) return null;

        HistoryStep mergedStep = null;

        /* iterate top -> bottom */
        for (int i = this.undos.size() - 1; i >= 0; i--) {
            HistoryStep current = this.undos.get(i);

            if (matchesMarkers(current.markers, mode, markers)) {

                if (mergedStep == null) {
                    /* first matching step (top-most) */
                    mergedStep = new HistoryStep(
                            current.stepTitle,
                            current.markers,
                            current.steps
                    );
                } else {
                    /* Add current steps to the bottom of stack, so undo order is preserved */
                    mergedStep.steps.addAll(0, current.steps);
                    mergedStep.markers.addAll(current.markers);
                }

                this.undos.remove(i);

            } else if (!popAll && mergedStep != null) {
                /*
                 * consecutive-only: break only when the method started matching (mergedStep != null)
                 * search until we find the first matching end until it does not match anymore
                 */
                break;
            }
        }

        if (mergedStep == null) return null;

        return new UndoRedoStep(mergedStep, this.stepCommand(relative, mergedStep, context, getContextForOT, true), true);
    }

    protected boolean matchesMarkers(Set<HistoryMarker> toCompareTo,
                                   BooleanOperand mode,
                                   HistoryMarker... filter) {
        if (filter == null || filter.length == 0) return false;

        long matches = Arrays.stream(filter)
                             .filter(toCompareTo::contains)
                             .count();

        return switch (mode) {
            case OR  -> matches >= 1;
            case AND -> matches == filter.length;
            case XOR -> matches == 1;
        };
    }


    protected IControllerCommand stepCommand(BaseValueHierarchy relative, HistoryStep step,
                                             IContext context, BiFunction<OperatorType, IContext, IContext> getContextForOT,
                                             boolean isUndo) {
        return new IControllerCommand() {
            @Override
            public void execute() throws UserException {
                Stack<StepData> steps = new Stack<>();
                steps.addAll(step.steps);
                /*
                 * Go through the steps and first undo the data operations and then the operator
                 */
                while (!steps.isEmpty()) {
                    StepData step = steps.pop();
                    Stack<DataOperationLog.RelativeEntry> stack = DataOperationLog.getStackRelativeTo(relative, step.log);
                    while (!stack.isEmpty()) {
                        DataOperationLog.RelativeEntry entry = stack.pop();

                        BaseStructure node;
                        if (entry.getPath() == null) {
                            node = relative;
                        } else {
                            node = relative.getChildByUniquePath(entry.getPath());
                        }

                        //TODO maybe not good idea to throw exception
                        if (node == null) throw new UserException("Could not undo as the data node was not found.");

                        IDataOperation op = entry.getOperation();

                        if (!op.isCorrectType(node) || !op.undo(node)) {
                            throw new UserException("Could not undo as one step failed.");
                        }
                    }

                    if (step.op != null) {
                        if (isUndo) step.op.undo(getContextForOT.apply(step.ot, context));
                        else step.op.redo(getContextForOT.apply(step.ot, context));
                    }
                }
            }

            @Override
            public List<BaseStructure> getAffectedNodes() {
                List<BaseStructure> nodes = new LinkedList<>();
                Stack<StepData> steps = new Stack<>();
                steps.addAll(step.steps);
                /*
                 * Go through the steps and first undo the data operations and then the operator
                 */
                while (!steps.isEmpty()) {
                    StepData step = steps.pop();

                    Stack<DataOperationLog.RelativeEntry> stack = DataOperationLog.getStackRelativeTo(relative, step.log);
                    while (stack.isEmpty()) {
                        DataOperationLog.RelativeEntry entry = stack.pop();

                        BaseStructure node;
                        if (entry.getPath() == null) {
                            nodes.add(relative);
                        } else {
                            nodes.add(relative.getChildByUniquePath(entry.getPath()));
                        }
                    }
                }

                return nodes;
            }
        };
    }




    /**
     * Compress the given data operation log by using a registry of strategies
     * to check for mergeability and parallelity and other facrtors.
     * <br><br>
     * Important to note that this method changes recorded data operation commands, so it is not guaranteed / bug free
     * that this might always be 100% accurate.
     * @param log
     * @return a new compressed stack.
     */
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
            for (int j = 0; j < parallel.size(); j++) {
                DataOperationLog.Entry cachedParallelEntry = parallel.get(j);
                if (!this.compressionStrategyRegistry.canBeParallel(cachedParallelEntry.getOperation(), nextEntry.getOperation())) {
                    notParallel = true;
                }

                MergeStrategy mergeStrategy = this.compressionStrategyRegistry.getMergeStrategy(cachedParallelEntry.getOperation().getClass(), nextEntry.getOperation().getClass());
                IDataOperation mergedOp;
                /* merge strategy returns same type as current,
                 * assume that is still parallel to the cache, otherwise another for loop is required
                 * TODO review
                 */
                if (mergeStrategy != null && (mergedOp = mergeStrategy.merge(cachedParallelEntry.getOperation(), nextEntry.getOperation())) != null) {
                    nextEntry = new HistoryLogEntry(cachedParallelEntry.getTimetamp(), mergedOp, cachedParallelEntry.getPath());
                    /* assume when merging onto a parallel node that it stays parallel to the cache */
                    notParallel = false;
                    merged = true;
                    parallel.set(j, nextEntry);
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




    /**
     * One History step. One step can have multiple operator/log steps, if they were merged because of equal markers.
     */
    protected class HistoryStep {
        private final String stepTitle;
        private final Set<HistoryMarker> markers = new HashSet<>();
        private final Stack<StepData> steps = new Stack<>();

        protected HistoryStep(String stepTitle, StepData data, HistoryMarker... marker) {
            this.stepTitle = stepTitle;
            this.steps.push(data);
            if (marker.length > 0) this.markers.addAll(Set.of(marker));
        }

        protected HistoryStep(String stepTitle, StepData data, Set<HistoryMarker> marker) {
            this.stepTitle = stepTitle;
            this.steps.push(data);
            this.markers.addAll(marker);
        }

        protected HistoryStep(String stepTitle, Set<HistoryMarker> marker, Stack<StepData> steps) {
            this.stepTitle = stepTitle;
            this.steps.addAll(steps);
            this.markers.addAll(marker);
        }

        protected HistoryStep(String stepTitle, StepData... step) {
            this.stepTitle = stepTitle;
            this.steps.addAll(List.of(step));
        }

        public String getStepTitle() {
            return stepTitle;
        }

        public Set<HistoryMarker> getMarkers() {
            return new HashSet<>(this.markers);
        }

        public Stack<StepData> getSteps() {
            Stack<StepData> copy = new Stack<>();
            copy.addAll(this.steps);
            return copy;
        }
    }

    /**
     * The atomic history step with execution logic to undo data operations.
     * StepData 0..1-->OperatorType/UndoCommand
     * @param ot
     * @param op
     * @param props
     * @param stepTitle
     * @param log
     */
    protected record StepData(@Nullable OperatorType ot,
                              @Nullable IUndoOperator.IUndoOperatorCommand op,
                              @Nullable OperatorProperties props,
                              String stepTitle,
                              Stack<DataOperationLog.Entry> log) {
        public StepData(OperatorType ot, IUndoOperator.IUndoOperatorCommand op, OperatorProperties props, String stepTitle) {
            this(ot, op, props, stepTitle, new Stack<>());
        }

        public StepData(String stepTitle, Stack<DataOperationLog.Entry> log) {
            this(null, null, null, stepTitle, new Stack<>());
        }
    }

    /* subclass to access timestamp parameter of constructor */
    protected static class HistoryLogEntry extends DataOperationLog.Entry {
        protected HistoryLogEntry(long timestamp, IDataOperation op, ValuePath path) {
            super(timestamp, path, op);
        }
    }

    /**
     * Interface for marker objects to merge mulitple history steps.
     * <br><br>
     * Is an interface for better organisation, readability and future use cases where a marker
     * might provide more information.
     * Currently only has default hash and equals method.
     */
    public interface HistoryMarker {
        boolean equals(Object marker);
        int hashCode();
    }

    /**
     * For performing an undo/redo step and inserting it back into the HistoryManager as redo/undo.
     */
    public final static class UndoRedoStep {
        protected final IControllerCommand command;
        protected final HistoryStep historyStep;
        /**
         * For the HistoryManager to verify that when pushing into redo stack the step is from undo stack.
         */
        protected final boolean isUndo;

        /**
         * Private so only history manager can instantiate. This ensures that only an undo step created by this history can be inserted
         * as redo step.
         */
        private UndoRedoStep(HistoryStep step, IControllerCommand command, boolean isUndo) {
            this.command = command;
            this.isUndo = isUndo;
            this.historyStep = step;
        }

        public IControllerCommand getCommand() {
            return this.command;
        }

        public String getTitle() {
            return this.historyStep.stepTitle;
        }
    }
}