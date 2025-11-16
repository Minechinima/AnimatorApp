package com.minechinima.model.logging;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.operations.IDataOperation;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for logging a data operation and store it in logs.
 * @implNote Do not store a BaseStructure reference here. This could end up dangerous as the log contains a link to this,
 * which might lead to memory leaks.
 */
public class DataOperationBus {
    /**
     * The loggers that should store operations logged here.
     * @implNote Do not expose this. Only this logger should have access to it to log changes.
     */
    private final Set<DataOperationLog> loggers = new HashSet<>();

    /**
     * Register a log that should be filled with operations logged by this.
     * @param log
     */
    public final void registerLog(DataOperationLog log) {
        if (log.isClosed()) {
            throw new IllegalArgumentException("Cannot register a log that is closed.");
        }
        this.loggers.add(log);
        log.addLogger(this);
    }

    /**
     * Only remove the log, nothing more.
     * @param log
     * @implNote Package-private so only {@link DataOperationLog} can remove itself automatically when it ended.
     */
    final void removeLog(DataOperationLog log) {
        this.loggers.remove(log);
    }

    /**
     * Log a data operation that happened on the given context
     * @param context
     * @param dataOperation
     * @implNote this is protected to secure who can log changes.
     * The goal is to prevent any external system other than the source of the data operation from logging.
     * Subclasses need to override this and open it for their context. Ideally, they use package-private to restrict logging
     * to the appropriate classes.
     */
    protected void logDataOperation(BaseStructure context, IDataOperation<?> dataOperation) {
        for (DataOperationLog logger : this.loggers) {
            logger.pushDataOperation(context.getFullPath(), dataOperation);
        }
    }
}
