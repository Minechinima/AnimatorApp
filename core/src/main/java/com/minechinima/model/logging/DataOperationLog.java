package com.minechinima.model.logging;

import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.utils.ICopy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A log of data operations.
 * The only purpose of this class is to provide the log and close it to stop recording new log entries.
 */
public class DataOperationLog {
    /**
     * @implNote Do not expose the valuepath instances
     */
    private final Stack<Entry> log = new Stack<>();
    /**
     * A cache of all logged data operation instances to avoid logging one instance multiple times.
     */
    private final Set<IDataOperation<?>> cache = Collections.newSetFromMap(new IdentityHashMap<>());
    private boolean end;
    private boolean paused;
    private final Set<DataOperationBus> loggers = new HashSet<>();

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

    public static Stack<RelativeEntry> getStackRelativeTo(BaseValueHierarchy<?> relative, Stack<Entry> stack) {
        Stack<RelativeEntry> copy = new Stack<>();

        ValuePath relativeFullPath = relative.getFullPath();

        for (Entry entry : stack) {
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

    /**
     *
     * @param relative
     * @return a copy of the log where only data operations
     * are included that are on the relative node or on children of the relative node.
     */
    public Stack<RelativeEntry> getLogRelativeTo(BaseValueHierarchy<?> relative) {
        return getStackRelativeTo(relative, this.log);
    }

    /**
     * End the log and remove from all logging services. After calling this, no more changes can be recorded.
     */
    public final void close() {
        this.end = true;
        for (DataOperationBus logger : this.loggers) {
            logger.removeLog(this);
        }
        this.loggers.clear();
        this.cache.clear();
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }

    /**
     * @return true if this log is closed and cannot record anymore.
     */
    public boolean isClosed() {
        return this.end;
    }

    /**
     * @param logger
     * @implNote Package-private so only {@link DataOperationBus} can add itself to the log.
     * This should only happen when there is a link between a log and a logger.
     */
    final void addLogger(DataOperationBus logger) {
        this.loggers.add(logger);
    }

    /**
     * Pushes the given data operation with the given full path into the stack.
     * @param fullPath the full path to the node where the operation was executed.<br>
     *                 <b>It needs to be the uncleaned path that includes all nodes' path segments, even empties.</b>
     * @param dataOperation
     * @throws IllegalArgumentException
     * @implNote Package-private so only the logger {@link DataOperationBus} can log changes.
     * The goal is to make this more secure that no other external system could inject log entries that were not triggered
     * by the source.
     * This also removes the logging/recording from the public interface of this log class, making it cleaner.
     */
    void pushDataOperation(ValuePath fullPath, IDataOperation<?> dataOperation) {
        if (this.end || this.paused) return;

        /*
         * We can't recover and fix this ourselves.
         * Is the order correct, do we ignore it, do we merge it?
         * This hints at a bad logging registration.
         */
        if (this.cache.contains(dataOperation)) {
            throw new IllegalArgumentException("Tried to log a data operation instance (reference equal) that was already logged.");
        }

        this.cache.add(dataOperation);

        // Deactivated, as the log should contain all operations for full transparency.
        // This might impact performance when iterating through it tho.
        // if (!this.log.empty() && this.log.peek().operation.merge(dataOperation)) {
        //    return;
        //}

        /*
         * Just plainly record any changes, no matter whether that node is actually addressable in the tree.
         * If the node is not addressable and triggers a change, that is rather an indication of a wrongly built
         * tree structure.
         */
        this.log.push(new Entry(fullPath.copy(), dataOperation));
    }

    protected static abstract class AbstractEntry {
        protected final long timetamp;
        /**
         * @implNote Never expose this instance directly, so no one outside the log
         * can change the original {@link DataOperationLog} path.
         * Always copy it.
         */
        protected final ValuePath path;
        protected final IDataOperation<?> operation;

        public AbstractEntry(long timetamp, ValuePath path, IDataOperation<?> operation) {
            this.timetamp = timetamp;
            this.path = path == null ? null : path.copy();
            this.operation = operation;
        }

        @NotNull
        public ValuePath getPath() {
            return this.path.copy();
        }

        public long getTimetamp() {
            return this.timetamp;
        }

        public IDataOperation<?> getOperation() {
            return this.operation;
        }
    }

    public static class Entry extends AbstractEntry implements ICopy<Entry> {
        public Entry(@NotNull ValuePath path, IDataOperation<?> operation) {
            this(System.currentTimeMillis(), path, operation);
        }

        protected Entry(long timetamp, ValuePath path, IDataOperation<?> operation) {
            super(timetamp, path, operation);
        }

        @Override
        public Entry copy() {
            return new Entry(this.timetamp, this.path, this.operation);
        }
    }

    public static class RelativeEntry extends AbstractEntry implements ICopy<RelativeEntry> {
        public RelativeEntry(@Nullable ValuePath path, IDataOperation<?> operation) {
            this(System.currentTimeMillis(), path, operation);
        }

        public RelativeEntry(IDataOperation<?> operation) {
            this(System.currentTimeMillis(), null, operation);
        }

        private RelativeEntry(long timetamp, @Nullable ValuePath path, IDataOperation<?> operation) {
            super(timetamp, path, operation);
        }

        /**
         * @return null if the entry's operation is to be performed on the target node.
         */
        @Nullable
        @Override
        public ValuePath getPath() {
            return this.path.copy();
        }

        @Override
        public RelativeEntry copy() {
            return new RelativeEntry(this.timetamp, this.path, this.operation);
        }
    }
}
