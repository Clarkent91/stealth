/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql;

import it.stealth.sql.wrappers.ConnectionWrapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Objects;

/**
 * Manages a transaction with the database through
 * operations performed on a specific database connection.
 * <p>The commit or rollback of the transaction is made in
 * closing based on the value of the <i>"complete"</i>
 * property that determines its outcome.</p>
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public class Transaction extends ConnectionWrapper
{
    /**
     * Indicates if the transaction can be
     * committed at the end of the transaction.
     */
    private boolean mComplete;
    
    /**
     * Indicates whether to close the internal
     * connection when closing the transaction.
     */
    private final boolean mCloseInner;
    
    /**
     * Defines the parameter relating to the database connection.
     */
    private static final String DbConnParam = "dbConn";
    
    /**
     * Exception message raised when the database to which
     * the connection is pointing does not support transactions.
     */
    private static final String NotSupported = "Transactions not supported.";
    
    /**
     * Exception message raised when trying to create a
     * transaction using another transaction as a connection object.
     */
    private static final String NestedTrans = "Unable to create nested Transaction objects.";
    
    /**
     * Exception message raised when attempting to enable autocommit for a transaction.
     */
    private static final String UnableEnableAutoCommit = "Failed to set autocommit for a transaction.";
    
    /**
     * Create a new {@link Transaction} using
     * a certain connection to the database.
     * 
     * @param dbConn database connection on which to start the transaction.
     * @throws NullPointerException if {@code dbConn} is {@code null}.
     * @throws IllegalArgumentException if {@code dbConn} is a transaction.
     * @throws SQLException if a database access error occurs or
     *         this method is called on a closed connection.
     */
    public Transaction(final Connection dbConn) throws SQLException {
        // Invoke the overloaded constructor
        this(dbConn, -1, true);
    }
    
    /**
     * Create a new {@link Transaction} using
     * a certain connection to the database.
     * 
     * @param dbConn database connection on
     *        which to start the transaction.
     * @param closeConnection if {@code true}, it closes the
     *        internal connection in the closing phase.
     * @throws NullPointerException if {@code dbConn} is {@code null}.
     * @throws IllegalArgumentException if {@code dbConn} is a transaction.
     * @throws SQLException if a database access error occurs or
     *         this method is called on a closed connection.
     */
    public Transaction(final Connection dbConn, final
            boolean closeConnection) throws SQLException {
        // Invoke the overloaded constructor
        this(dbConn, -1, closeConnection);
    }
    
    /**
     * Create a new {@link Transaction} using a certain connection
     * to the database and specifying the level of isolation required.
     * <p>The constants defined in the interface {@link Transaction}
     * are the possible transaction isolation levels.</p><p>(Note that
     * {@link Transaction#TRANSACTION_NONE} cannot be used because it
     * specifies that transactions are not supported).</p>
     * 
     * @param dbConn database connection on which to start the transaction.
     * @param level one of the following {@link Transaction} constants:
     *        {@link Transaction#TRANSACTION_READ_UNCOMMITTED},
     *        {@link Transaction#TRANSACTION_READ_COMMITTED},
     *        {@link Transaction#TRANSACTION_REPEATABLE_READ},
     *        or {@link Transaction#TRANSACTION_SERIALIZABLE}.
     * @throws NullPointerException if {@code dbConn} is {@code null}.
     * @throws IllegalArgumentException if {@code dbConn} is a transaction.
     * @throws SQLException if a database access error occurs or
     *         this method is called on a closed connection.
     */
    public Transaction(final Connection dbConn, final int level) throws SQLException {
        // Invoke the overloaded constructor
        this(dbConn, level, true);
    }
    
    /**
     * Create a new {@link Transaction} using a certain connection
     * to the database and specifying the level of isolation required.
     * <p>The constants defined in the interface {@link Transaction}
     * are the possible transaction isolation levels.</p><p>(Note that
     * {@link Transaction#TRANSACTION_NONE} cannot be used because it
     * specifies that transactions are not supported).</p>
     * 
     * @param dbConn database connection on which to start the transaction.
     * @param level one of the following {@link Transaction} constants:
     *        {@link Transaction#TRANSACTION_READ_UNCOMMITTED},
     *        {@link Transaction#TRANSACTION_READ_COMMITTED},
     *        {@link Transaction#TRANSACTION_REPEATABLE_READ},
     *        or {@link Transaction#TRANSACTION_SERIALIZABLE}.
     * @param closeConnection if {@code true}, it closes the
     *        internal connection in the closing phase.
     * @throws NullPointerException if {@code dbConn} is {@code null}.
     * @throws IllegalArgumentException if {@code dbConn} is a transaction.
     * @throws SQLException if a database access error occurs or
     *         this method is called on a closed connection.
     */
    public Transaction(final Connection dbConn, final int level,
            final boolean closeConnection) throws SQLException {
        // Invokes the superclass constructor by
        // checking the database connection is not null
        super(Objects.requireNonNull(dbConn, Transaction.DbConnParam));
        // Stores the flag indicating whether
        // to close the internal connection
        this.mCloseInner = closeConnection;
        // Check that the connection provided
        // is not actually another transaction
        if (dbConn instanceof Transaction) {
            // Raises an exception by indicating that it is
            // not possible to create nested Transaction objects
            throw new IllegalArgumentException(Transaction.NestedTrans);
        } else {
            // Starts a try-catch block
            try {
                // Check if the transactions are supported
                if (Transaction.isTransactionSupported(dbConn)) {
                    // Check to specify the isolation level
                    if (level >= Connection.TRANSACTION_NONE) {
                        // Set the isolation level of the transaction
                        dbConn.setTransactionIsolation(level);
                    }
                    // Sets the auto-commit of the connection
                    dbConn.setAutoCommit(false);
                } else {
                    // Raises an exception indicating
                    // that transactions are not supported
                    throw new SQLException(Transaction.NotSupported);
                }
            }
            // Catch any SQL exception
            catch (final SQLException ex1) {
                // Check if the internal
                // connection must be closed
                if (this.mCloseInner) {
                    // Starts a try-catch block
                    try {
                        // Closes the database connection
                        dbConn.close();
                    }
                    // Catch any SQL exception
                    catch (final SQLException ex2) {
                        // Suppress the current exception
                        // to highlight the previous one
                        ex1.addSuppressed(ex2);
                    }
                }
                // Raise the previous
                // exception again
                throw ex1;
            }
        }
    }
    
    /**
     * Releases this {@link Transaction} object's database and JDBC resources
     * immediately instead of waiting for them to be automatically released.
     * <p>Calling the method {@link #close() close()} on a {@link Transaction}
     * object that is already closed is a no-op.</p><p>Unlike classic
     * {@link Connection} objects, the transaction outcome (that is,
     * the commit or rollback) will be performed based on the value
     * specified by the {@link #isComplete()} method.</p>
     *
     * @exception SQLException if a database access error occurs.
     */
    @Override
    public synchronized void close() throws SQLException {
        // Check that the underlying connection
        // has not already been closed
        if (!this.mInner.isClosed()) {
            // Starts a try-catch block
            try {
                // Check whether to commit
                // the pending transaction
                if (this.mComplete) {
                    // Commit the transaction
                    this.mInner.commit();
                } else {
                    // Rollback the transaction
                    this.mInner.rollback();
                }
            }
            // Catch any SQL exception
            catch (final SQLException ex1) {
                // Check if the internal
                // connection must be closed
                if (this.mCloseInner) {
                    // Starts a try-catch block
                    try {
                        // Closes the database connection
                        this.mInner.close();
                    }
                    // Catch any SQL exception
                    catch (final SQLException ex2) {
                        // Suppress the current exception
                        // to highlight the previous one
                        ex1.addSuppressed(ex2);
                        // Raise the previous
                        // exception again
                        throw ex1;
                    }
                }
            } finally {
                // Check if the internal
                // connection must be closed
                if (this.mCloseInner) {
                    // Closes the database connection
                    this.mInner.close();
                }
            }
        }
    }
    
    /**
     * Overrides the {@link #setAutoCommit(boolean)} method
     * to avoid the activation of autocommit for a transaction.
     * 
     * @param autoCommit if {@code true}, it generates an exception.
     * @throws UnsupportedOperationException if {@code autoCommit}
     *                                       is {@code true}.
     */
    @Override
    public final void setAutoCommit(final boolean autoCommit) {
        // Check if it is an attempt to enable autocommit
        if (autoCommit) {
            // Raises an exception indicating that it is
            // impossible to enable autocommit for a transaction
            throw new UnsupportedOperationException(
                    Transaction.UnableEnableAutoCommit);
        }
    }
    
    /**
     * Removes the specified {@link Savepoint} and subsequent {@link Savepoint}
     * objects from the current transaction. Any reference to the savepoint after
     * it have been removed will cause an {@link SQLException} to be thrown.
     *
     * @param savepoint the {@link Savepoint} object to be removed.
     * @throws SQLException if a database access error occurs, this method is
     *         called on a closed connection or the given {@link Savepoint}
     *         object is not a valid savepoint in the current transaction.
     */
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.releaseSavepoint(savepoint);
    }
    
    /**
     * Undoes all changes made after the
     * given {@link Savepoint} object was set.
     *
     * @param savepoint the {@link Savepoint} object to roll back to.
     * @throws SQLException if a database access error occurs, this method is
     *         called while participating in a distributed transaction, this
     *         method is called on a closed connection, the {@link Savepoint}
     *         object is no longer valid.
     */
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.rollback(savepoint);
    }
    
    /**
     * Retrieves whether this {@link Transaction} object has been closed.
     * <p>A transaction is closed if the method {@link #close() close()} has
     * been called on it or if certain fatal errors have occurred. This method
     * is guaranteed to return {@code true} only when it is called after the
     * method {@link Transaction#close()} has been called.</p><p>This method
     * generally cannot be called to determine whether a transaction to a
     * database is valid or invalid. A typical client can determine that
     * a connection is invalid by catching any exceptions that might be
     * thrown when an operation is attempted.</p>
     *
     * @return {@code true} if this {@link Transaction} object
     *         is closed; {@code false} if it is still open.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public final boolean isClosed() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.isClosed();
    }
    
    /**
     * Private method that determines if transactions are supported.
     * 
     * @param dbConn database connection to be checked.
     * @return {@code true} if the transactions are supported.
     * @throws SQLException if a database access error occurs or
     *         this method is called on a closed connection.
     */
    private static boolean isTransactionSupported(final
            Connection dbConn) throws SQLException {
        // Check if the transactions are supported
        return dbConn.getTransactionIsolation() !=
                Connection.TRANSACTION_NONE;
    }
    
    /**
     * Sets the value that determines the outcome of the transaction.
     * <p>If {@code true} is provided then the transaction will be
     * committed during the closing phase. Otherwise, in closing,
     * the transaction will be rolled up.</p>
     *
     * @param value outcome of the transaction.
     * @see #isComplete()
     */
    @Deprecated
    public void setComplete(final boolean value) {
        // Stores the value supplied
        this.mComplete = value;
    }
    
    public void complete() {
        this.mComplete = true;
    }
    
    /**
     * Undoes all changes made in the current transaction
     * and releases any database locks currently held
     * by this {@link Transaction} object.
     * 
     * @throws SQLException a database access error occurs, this method is
     *         called while participating in a distributed transaction,
     *         this method is called on a closed connection.
     */
    @Override
    public void rollback() throws SQLException {
        // Invokes the underlying connection method
        this.mInner.rollback();
    }
    
    /**
     * Makes all changes made since the previous
     * commit/rollback permanent and releases any database
     * locks currently held by this {@link Transaction} object.
     * 
     * @throws SQLException if a database access error occurs, this method
     *         is called while participating in a distributed transaction,
     *         if this method is called on a closed connection or this
     *         {@link Connection} object is in auto-commit mode.
     */
    @Override
    public void commit() throws SQLException {
        // Invokes the underlying connection method
        this.mInner.commit();
    }
    
    /**
     * Indicates whether the transaction will end with a commit or rollback.
     * <p>Based on this value, during the closing phase, this transaction
     * will end with a commit or a rollback.</p>
     * 
     * @return if {@code true} then the transaction will end
     *         with a commit, {@code false} with a rollback.
     * @see #setComplete(boolean)
     */
    public boolean isComplete() {
        // Returns the stored value
        return this.mComplete;
    }
}
