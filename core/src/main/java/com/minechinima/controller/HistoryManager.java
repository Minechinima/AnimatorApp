package com.minechinima.controller;

import com.minechinima.app.IGenericApplication;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.dataoperations.DataOperationLog;
import com.minechinima.model.dataoperations.operations.IDataOperation;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.utils.Pair;

import java.util.Stack;

public class HistoryManager {
    protected Stack<HistoryStep> undos = new Stack<>();
    protected Stack<HistoryStep> redos = new Stack<>();
    private final IGenericApplication<?> app;

    public HistoryManager(IGenericApplication<?> owner) {
        this.app = owner;
    }

    public void pushTransaction(Transaction transaction) {
        this.pushTransaction(this.undos, transaction);
    }

    private void pushTransaction(Stack<HistoryStep> toPush, Transaction transaction) {
        if (transaction.getState() != Transaction.State.COMMITTED) {
            return;
        }

        toPush.push(new HistoryStep(transaction.getLocalizedTitle(), transaction.getLog()));
    }

    public ControllerResult undo(final BaseValueHierarchy<?> relative) {
        if (this.undos.empty()) {
            return new ControllerResult(ControllerResult.State.SUCCESS, "Nothing to undo.");
        } else if (this.app.getController().getCurrentTransaction() != null) {
            return new ControllerResult(ControllerResult.State.ERROR, "Cannot undo while an action is being performed.");
        }

        HistoryStep step = this.undos.pop();
        ControllerResult result = this.app.getController().startTransaction("Undo: " + step.stepTitle, () -> {
            this.applyHistoryStep(relative, step);
        });
        if (result.state() == ControllerResult.State.SUCCESS) {
            Transaction transaction = this.app.getController().getCurrentTransaction();
            this.app.getController().commitCurrentTransaction();
            this.pushTransaction(this.redos, transaction);
        }

        return result;
    }

    public ControllerResult redo(Controller controller) {
        return null;
    }

    private void applyHistoryStep(BaseValueHierarchy<?> relative, HistoryStep historyStep) throws UserException {
        Stack<Pair<String, IDataOperation>> step = new Stack<>();

        for (DataOperationLog.RelativeEntry entry : historyStep.log().getLogRelativeTo(relative)) {
            ValuePath path = entry.getRelativePath();
            String stringPath = null;
            if (path != null) {
                stringPath = path.toUniquePath();
            }
            step.push(new Pair<>(stringPath, entry.getOperation()));
        }

        while (!step.isEmpty()) {
            Pair<String, IDataOperation> op = step.pop();
            BaseStructure context = op.getA() == null ? relative : relative.getChildByUniquePath(op.getA());
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

    protected record HistoryStep(String stepTitle, DataOperationLog log) { }
}
