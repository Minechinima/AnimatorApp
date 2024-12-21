package com.minechinima.controller;

import com.minechinima.model.logging.DataOperationLog;

import java.util.Stack;

/**
 * This class represents a database transaction.
 * <br><br>
 * A database transaction is a logical unit that includes several low-level steps to perform an operation.<br>
 * If one of the steps fails, the whole transaction fails, and the database must be reverted, so it stays unchanged.
 * <br><br>
 * Note for this architecture that transactions are only for modifying operations and not read operations.
 * <br><br>
 * This transaction contains a log of all low-level steps needed to perform the operation and a {@link State}
 */
public class Transaction {
    private final DataOperationLog log = new DataOperationLog();
    private final long timestamp;
    private State state = State.ACTIVE;
    /**
     * @implNote Effectively final - only mutable for package
     */
    private String localizedTitle;

    public Transaction(String localizedTitle) {
        this.localizedTitle = localizedTitle;
        this.timestamp = System.currentTimeMillis();
    }

    Transaction() {
        this.timestamp = System.currentTimeMillis();
    }

    public Stack<DataOperationLog.Entry> getDataOperationStack() {
        return this.log.getLog();
    }

    void setTitle(String title) {
        this.localizedTitle = title;
    }

    /**
     * @return the log of this transaction
     * @implNote package-private so no one outside controller module can register this log and do weird things with it
     * as it should only contain what the controller changes.
     */
    DataOperationLog getLog() {
        return this.log;
    }

    public String getLocalizedTitle() {
        return this.localizedTitle;
    }

    public State getState() {
        return this.state;
    }

    /**
     * Transition to aborted state. This closes the transaction.
     * @implNote this is default so it can only be modified by {@link Controller}, which is in the same package.
     * Also prevent subclasses from changing the access.
     */
    void abortedState() {
        if (this.state == State.ACTIVE) {
            this.state = State.ABORTED;
            this.log.close();
        }
    }


    /**
     * Transition to committed state, indicating a successful transaction. This closes the transaction.
     * @implNote this is default so it can only be modified by {@link Controller}, which is in the same package.
     * Also prevent subclasses from changing the access.
     */
    void committedState() {
        if (this.state == State.ACTIVE) {
            this.state = State.COMMITTED;
            this.log.close();
        }
    }

    /**
     * The possible states of a {@link Transaction}.
     */
    public enum State {
        /**
         * No errors happened during the transaction and the server database state is changed.
         */
        COMMITTED,
        /**
         * The transaction is still running, the server database has not changed.
         * Though, the local database might have changed.
         */
        ACTIVE,
        /**
         * The transaction was aborted and the low-level steps reverted from the local database. The server database was not changed.
         */
        ABORTED
    }
}
