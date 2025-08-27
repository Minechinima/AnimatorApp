package com.minechinima.model.logging;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.TestOnly;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Stack;

/**
 * This is the old implementation of the HistoryManager that was simultaneously the logger.
 * This is here only for testing purposes.
 */
@TestOnly
public class DummyHistoryManager extends DataOperationLog {
    protected Stack<Map.Entry<String, IDataOperation>> undoCache = new Stack<>();
    protected Stack<Map.Entry<String, IDataOperation>> redoCache = new Stack<>();
    protected final Stack<Stack<Map.Entry<String, IDataOperation>>> undos = new Stack<>();
    protected final Stack<Stack<Map.Entry<String, IDataOperation>>> redos = new Stack<>();
    protected final BaseValueHierarchy<?> observed;
    protected boolean recordUndoAsRedo = false;
    protected boolean isRedoing = false;
    /**
     * Whether this HistoryManager is recording undo redo steps.
     * Useful to deactivate when the tree state is synced via networking, as those are no user history actions.
     */
    protected boolean isRecording = true;

    public DummyHistoryManager(BaseValueHierarchy<? extends BaseStructure> root) {
        this(root, true);
    }

    public DummyHistoryManager(BaseValueHierarchy<? extends BaseStructure> root, boolean isRecording) {
        this.isRecording = isRecording;
        this.observed = root;
    }

    public BaseValueHierarchy<? extends BaseStructure> getObservedRoot() {
        return this.observed;
    }

    public boolean isRecording() {
        return this.isRecording;
    }

    public void setRecording(boolean isRecording) {
        this.isRecording = isRecording;
    }

    /**
     * Push everything that is in the undo cache into the history buffer.
     */
    public void commitUndoStep() {
        if (this.undoCache.empty()) return;

        /* iterate in insertion order to check merging of undos
        Stack<Map.Entry<String, IUndo>> currentUndo;
        if (!this.undos.empty() && !this.undos.peek().empty()) {
            currentUndo = this.undos.peek();
            for (Iterator<Map.Entry<String, IUndo>> it = this.undoCache.iterator(); it.hasNext();) {
                Map.Entry<String, IUndo> entry = it.next();
                if (currentUndo.peek().getValue().canMerge(entry.getValue())) {
                    currentUndo.peek().getValue().merge(entry.getValue());
                    it.remove();
                }
            }
        } */

        if (!this.undoCache.empty()) this.undos.push(this.undoCache);
        this.undoCache = new Stack<>();
    }

    public void clearUndoCache() {
        this.undoCache = new Stack<>();
    }

    /**
     * Directly execute the undo steps in the undo cache and don't record them as redo steps.
     * Clears the undo cache.
     */
    public void rewindUndoCache() throws UserException {
        if (this.undoCache.empty()) return;
        boolean prev = this.recordUndoAsRedo;
        this.recordUndoAsRedo = false;
        try {
            this.applyUndoSteps((Stack<Map.Entry<String, IDataOperation>>) this.undoCache.clone());
        } catch (Exception e) {
            throw e;
        } finally {
            this.undoCache = new Stack<>();
            this.recordUndoAsRedo = prev;
        }
    }

    @Override
    void pushDataOperation(ValuePath path, IDataOperation<?> dataOperation) {
        if (!this.isRecording) return;
        ValuePath relativeFullPath = this.observed.getFullPath();
        ValuePath subtracted = path.subtractFromStart(relativeFullPath);
        if (subtracted == null) throw new IllegalArgumentException("Path is not relative to observed.");

        String uniquePath = subtracted.getSize() == 0 ? null : subtracted.toUniquePath();
        /*
         * Completely relative to the current tree state when undoing and redoing
         * Undoing will record a new redo step that captures the state as it was when undoing.
         * When redoing and undo step, it will capture a new state, as it might have changed with collaborative working.
         */
        if (this.recordUndoAsRedo) {
            this.push(this.redoCache, uniquePath, dataOperation);
        } else {
            if (!this.isRedoing) this.redos.clear();
            this.push(this.undoCache, uniquePath, dataOperation);
        }
    }

    protected void push(Stack<Map.Entry<String, IDataOperation>> cache, String uniquePath, IDataOperation step) {
        cache.push(new AbstractMap.SimpleEntry<>(uniquePath, step));
    }

    public void undo() throws UserException {
        if (this.undos.empty()) return;
        this.recordUndoAsRedo = true;
        boolean prevRecording = this.isRecording;
        this.isRecording = true;
        this.redoCache.clear();
        try {
            this.applyUndoSteps(this.undos.pop());
        } catch (Exception e) {
            throw e;
        } finally {
            this.recordUndoAsRedo = false;
            this.redos.push(this.redoCache);
            this.redoCache = new Stack<>();
            this.isRecording = prevRecording;
        }
    }

    public void redo() throws UserException {
        if (this.redos.empty()) return;
        this.isRedoing = true;
        boolean prevRecording = this.isRecording;
        this.isRecording = true;
        this.commitUndoStep();
        try {
            this.applyUndoSteps(this.redos.pop());
        } catch (Exception e) {
            throw e;
        } finally {
            this.isRedoing = false;
            this.commitUndoStep();
            this.isRecording = prevRecording;
        }
    }

    protected void applyUndoSteps(Stack<Map.Entry<String, IDataOperation>> steps) throws UserException {
        while (!steps.empty()) {
            Map.Entry<String, IDataOperation> entry = steps.pop();
            if (entry.getKey() == null) {
                entry.getValue().undo(this.observed);
            } else {
                BaseStructure child = this.observed.getChildByUniquePath(entry.getKey());
                if (child != null) {
                    entry.getValue().undo(child);
                }
            }
        }
    }

    public Stack<Stack<Map.Entry<String, IDataOperation>>> getUndos() {
        return undos;
    }

    public Stack<Stack<Map.Entry<String, IDataOperation>>> getRedos() {
        return redos;
    }

    public BaseValueHierarchy<?> getRoot() {
        return observed;
    }

    public boolean isRecordUndoAsRedo() {
        return recordUndoAsRedo;
    }

    public boolean isRedoing() {
        return isRedoing;
    }
}
