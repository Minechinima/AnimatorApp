package com.minechinima.controller;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.utils.ValuePath;

import java.util.Stack;

public class HistoryManager {
    protected Stack<HistoryStep> undos = new Stack<>();
    protected Stack<HistoryStep> redos = new Stack<>();
    private boolean pushTransactionToRedo;
    private boolean isRedoing;

    public void pushTransaction(Transaction transaction) {
        if (!this.pushTransactionToRedo && !this.isRedoing) this.redos.clear();
        this.pushTransaction(this.pushTransactionToRedo ? this.redos : this.undos, transaction);
    }

    private void pushTransaction(Stack<HistoryStep> toPush, Transaction transaction) {
        if (transaction.getState() != Transaction.State.COMMITTED) {
            throw new IllegalArgumentException("Cannot push an uncommitted transaction into the history.");
        }

        transaction.getLog().close();
        toPush.push(new HistoryStep(transaction.getLocalizedTitle(), transaction.getLog()));
    }

    //TODO think about HistoryManager dependency towards Controller. If Operator executes this it will be inside a transaction?
    public ControllerResult undo(final Controller controller, final BaseValueHierarchy<?> relative) {
        if (!controller.getDataTree().getRoot().isInSameTree(relative)) {
            throw new IllegalArgumentException("The relative element for the HistoryManager is not in the Controller data tree.");
        }

        if (this.undos.empty()) {
            return new ControllerResult(ControllerResult.State.SUCCESS, "Nothing to undo.");
        } else if (controller.isActive()) {
            return new ControllerResult(ControllerResult.State.ERROR, "Cannot undo while an action is being performed.");
        }

        HistoryStep step = this.undos.pop();
        this.pushTransactionToRedo = true;
        ControllerResult result = controller.executeNewTransaction(this,
                "Undo: " + step.stepTitle, () -> {
            this.applyHistoryStep(relative, step);
        });
        this.pushTransactionToRedo = false;
        return result;
    }

    public ControllerResult redo(final Controller controller, final BaseValueHierarchy<?> relative) {
        if (!controller.getDataTree().getRoot().isInSameTree(relative)) {
            throw new IllegalArgumentException("The relative element for the HistoryManager is not in the Controller data tree.");
        }

        if (this.redos.empty()) {
            return new ControllerResult(ControllerResult.State.SUCCESS, "Nothing to redo.");
        } else if (controller.isActive()) {
            return new ControllerResult(ControllerResult.State.ERROR, "Cannot undo while an action is being performed.");
        }

        this.isRedoing = true;
        HistoryStep step = this.redos.pop();
        ControllerResult result = controller.executeNewTransaction(this,
                "Redo: " + step.stepTitle, () -> {
                    this.applyHistoryStep(relative, step);
                });
        this.isRedoing = false;
        return result;
    }

    private void applyHistoryStep(BaseValueHierarchy<?> relative, HistoryStep historyStep) throws UserException {
        Stack<DataOperationLog.RelativeEntry> step = historyStep.log().getLogRelativeTo(relative);

        while (!step.isEmpty()) {
            DataOperationLog.RelativeEntry entry = step.pop();
            ValuePath path = entry.getRelativePath();
            IDataOperation operation = entry.getOperation();

            BaseStructure context = path == null ? relative : relative.getChildByUniquePath(path);
            if (context == null) {
                throw new UserException("Cannot revert transaction as one element cannot be found.");
            }

            if (!operation.isCorrectType(context)) {
                throw new UserException("Cannot revert step as the element that was defined cannot be handled by the data operation.");
            }

            try {
                operation.undo(context);
            } catch (UserException e) {
                throw e;
            } catch (RuntimeException e) {
                throw new UserException("Unexpected error.", e);
            }
        }
    }

    protected record HistoryStep(String stepTitle, DataOperationLog log) { }
}
