package com.minechinima.controller;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.dataoperations.DataOperationLog;
import com.minechinima.model.dataoperations.IDataOperation;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.utils.Pair;

import java.util.Stack;

public class HistoryManager {
    protected Stack<HistoryStep> undos = new Stack<>();
    protected Stack<HistoryStep> redos = new Stack<>();
    //TODO remove
    private final BaseValueHierarchy<?> observed;

    public HistoryManager(BaseValueHierarchy<?> observed) {
        this.observed = observed;
    }

    public BaseValueHierarchy<? extends BaseStructure> getObservedRoot() {
        return this.observed;
    }

    public void pushTransaction(Transaction transaction) {
        this.pushTransaction(this.undos, transaction);
    }

    private void pushTransaction(Stack<HistoryStep> toPush, Transaction transaction) {
        if (transaction.getState() != Transaction.State.COMMITTED) {
            return;
        }

        Stack<Pair<String, IDataOperation>> step = new Stack<>();

        for (DataOperationLog.RelativeEntry entry : transaction.getLog().getLogRelativeTo(this.observed)) {
            ValuePath path = entry.getRelativePath();
            String stringPath = null;
            if (path != null) {
                stringPath = path.toUniquePath();
            }
            step.push(new Pair<>(stringPath, entry.getOperation()));
        }

        toPush.push(new HistoryStep(transaction.getLocalizedTitle(), step));
    }

    public ControllerResult undo(Controller controller) {
        if (this.undos.empty()) {
            return new ControllerResult(ControllerResult.State.SUCCESS, "Nothing to undo.");
        } else if (controller.getCurrentTransaction() != null) {
            return new ControllerResult(ControllerResult.State.ERROR, "Cannot undo while an action is being performed.");
        }

        HistoryStep step = this.undos.pop();
        ControllerResult result = controller.startTransaction("Undo: " + step.stepTitle, () -> {
            this.applyHistoryStep(step);
        });
        if (result.state() == ControllerResult.State.SUCCESS) {
            Transaction transaction = controller.getCurrentTransaction();
            controller.commitCurrentTransaction();
            this.pushTransaction(this.redos, transaction);
        }

        return result;
    }

    public ControllerResult redo(Controller controller) {
        return null;
    }

    private void applyHistoryStep(HistoryStep step) throws UserException {
        while (!step.log.isEmpty()) {
            Pair<String, IDataOperation> op = step.log.pop();
            BaseStructure context = op.getA() == null ? this.observed : this.observed.getChildByUniquePath(op.getA());
            if (context == null) {
                throw new UserException("Cannot revert transaction as one element cannot be found.");
            }

            if (!op.getB().isCorrectType(context)) {
                throw new UserException("Cannot revert step as the element that was defined cannot be handled by the data operation.");
            }

            try {
                op.getB().undo(context);
            } catch (UserException e) {
                throw e;
            } catch (RuntimeException e) {
                throw new UserException("Unexpected error.", e);
            }
        }
    }

    protected record HistoryStep(String stepTitle, Stack<Pair<String, IDataOperation>> log) { }
}
