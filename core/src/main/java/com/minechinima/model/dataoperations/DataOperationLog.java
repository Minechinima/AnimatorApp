package com.minechinima.model.dataoperations;

import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.utils.ICopy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Only logs data operations. Nothing more fancy.
 */
public class DataOperationLog {
    /**
     * @implNote Do not expose the valuepath instances
     */
    private final Stack<Entry> log = new Stack<>();
    private boolean end;

    /**
     * End the log. After calling this, no more changes can be recorded.
     */
    public void endLog() {
        this.end = true;
    }

    /**
     * @return true if this log is closed and cannot record anymore.
     */
    public boolean isEnded() {
        return this.end;
    }

    /**
     * @param context
     * @param dataOperation
     * @throws IllegalStateException
     */
    public void recordDataOperation(BaseStructure context, IDataOperation dataOperation) {
        if (this.end) return;

        if (!this.log.empty() && this.log.peek().operation.merge(dataOperation)) {
            return;
        }

        /*
         * Just plainly record any changes, no matter whether that node is actually addressable in the tree.
         * If the node is not addressable and triggers a change, that is rather an indication of a wrongly built
         * tree structure.
         */
        this.log.push(new Entry(context.getFullPath(), dataOperation));
    }

    /**
     * @return copy of the log.
     */
    public Stack<Entry> getLog() {
        Stack<Entry> copy = new Stack<>();

        for (Entry entry : this.log) {
            copy.push(entry.copy());
        }

        return copy;
    }

    /**
     *
     * @param relative
     * @return a copy of the log where only data operations
     * are included that are on the relative node or on children of the relative node.
     */
    public Stack<RelativeEntry> getLogRelativeTo(BaseValueHierarchy<?> relative) {
        Stack<RelativeEntry> copy = new Stack<>();

        ValuePath relativeFullPath = relative.getFullPath();

        for (Entry entry : this.log) {
            if (entry.path.equals(relativeFullPath)) {
                copy.push(new RelativeEntry(entry.timetamp, null, entry.operation));
            } else {
                ValuePath subtracted = entry.path.subtractFromStart(relativeFullPath);
                if (subtracted != null) {
                    copy.push(new RelativeEntry(entry.timetamp, subtracted, entry.operation));
                }
            }
        }

        return copy;
    }

    protected static abstract class AbstractEntry {
        protected final long timetamp;
        /**
         * @implNote Never expose this instance directly, so no one outside the log can change it.
         * Always copy it.
         */
        protected final ValuePath path;
        protected final IDataOperation operation;

        public AbstractEntry(long timetamp, ValuePath path, IDataOperation operation) {
            this.timetamp = timetamp;
            this.path = path == null ? null : path.copy();
            this.operation = operation;
        }

        public long getTimetamp() {
            return this.timetamp;
        }

        public IDataOperation getOperation() {
            return this.operation;
        }
    }

    public static class Entry extends AbstractEntry implements ICopy<Entry> {
        public Entry(@NotNull ValuePath path, IDataOperation operation) {
            this(System.currentTimeMillis(), path, operation);
        }

        private Entry(long timetamp, ValuePath path, IDataOperation operation) {
            super(timetamp, path, operation);
        }

        /**
         * @return copy of full path
         */
        @NotNull
        public ValuePath getPath() {
            return this.path;
        }

        @Override
        public Entry copy() {
            return new Entry(this.timetamp, this.path, this.operation);
        }
    }

    public static class RelativeEntry extends AbstractEntry implements ICopy<RelativeEntry> {
        public RelativeEntry(@Nullable ValuePath path, IDataOperation operation) {
            this(System.currentTimeMillis(), path, operation);
        }

        public RelativeEntry(IDataOperation operation) {
            this(System.currentTimeMillis(), null, operation);
        }

        private RelativeEntry(long timetamp, @Nullable ValuePath path, IDataOperation operation) {
            super(timetamp, path, operation);
        }

        /**
         * @return null if the entry's operation is to be performed on the target node.
         */
        @Nullable
        public ValuePath getRelativePath() {
            return this.path;
        }

        @Override
        public RelativeEntry copy() {
            return new RelativeEntry(this.timetamp, this.path, this.operation);
        }
    }
}
