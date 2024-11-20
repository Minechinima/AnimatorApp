package com.minechinima.model.dataoperations;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.dataoperations.operations.IDataOperation;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for logging a data operation and store it in logs.
 * @implNote Do not store a BaseStructure reference here. This could end up dangerous as the log contains a link to this,
 * which might lead to memory leaks.
 */
public class DataOperationLogging {
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
        if (log.isEnded()) return;
        this.loggers.add(log);
        log.addLogger(this);
    }

    /**
     * Unregister a log and freezes it.
     * @param log
     */
    public final void unregisterLog(DataOperationLog log) {
        /*
         * This is part of the public interface of the logging system.
         * Therefore, it does more logic and ensures a log is frozen and cannot be reused
         * to create a mashup of different operations from different loggers.
         */
        log.freeze();
        this.loggers.remove(log);
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
