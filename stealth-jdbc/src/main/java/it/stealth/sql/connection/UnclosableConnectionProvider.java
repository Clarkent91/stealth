/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

/**
 * Implements a {@link DbConnectionProvider} wrapper that
 * keeps a connection open and inhibits its closing operation.
 * <p>The {@link DbConnectionProvider} supplied will be used a single time to
 * request an instance of the database connection, which in turn will be wrapped
 * to inhibit the closing operation.</p><p>The instance of this connection (a
 * {@link UnclosableConnection}) will then be maintained and provided with each
 * new call of the {@link #getDbConnection()} method.</p><p>To close the underlying
 * connection it's possibile to use {@link #dispose()} and {@link #close()} methods,
 * or otherwise let the connection be closed by the <i>Garbage Collectior</i> itself,
 * when the reference to this provider is lost.</p>
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public final class UnclosableConnectionProvider extends
        LazyInitializer<UnclosableConnection> implements
        DbConnectionProvider, AutoCloseable
{
    /**
     * Indicates if a connection previously
     * instanced by this provider has been closed.
     */
    private boolean mDisposed;
    
    /**
     * Indicates whether the connection reference was initialized.
     */
    private volatile boolean mInit;
    
    /**
     * Original provider that provides the connection to the database.
     */
    private final DbConnectionProvider mInner;
    
    /**
     * Creates a new instance of {@link UnclosableConnectionProvider}
     * that wraps a {@link DbConnectionProvider} object to return a
     * single connection to which the close operation was inhibited.
     * 
     * @param dbConnectionProvider original provider of the database connection.
     * @throws NullPointerException if {@code dbConnectionProvider} is {@code null}.
     */
    public UnclosableConnectionProvider(final DbConnectionProvider dbConnectionProvider) {
        // Stores the reference to the connection provider used internally
        this.mInner = Objects.requireNonNull(dbConnectionProvider);
        // Initialize the disposal flag
        this.mDisposed = false;
        // Initialize the initialization flag
        this.mInit = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UnclosableConnection initialize() throws ConcurrentException {
        // Declare a support reference
        final Connection dbConn;
        // Starts a try-catch block
        try {
            // Opens a connection to the database
            dbConn = this.mInner.getDbConnection();
            // Check if the connection is already closed
            this.mDisposed = dbConn.isClosed();
            // Mark this provider as initialized
            this.mInit = true;
        }
        // Catch any SQL exception
        catch (final SQLException sqlEx) {
            // Wraps the current exception
            // to pass it to the caller
            throw new ConcurrentException(sqlEx);
        }
        // Check if the connection provided is
        // already an instance of UnclosableConnection
        if (dbConn instanceof UnclosableConnection) {
            // Returns directly the connection provided
            return (UnclosableConnection) dbConn;
        } else {
            // Wraps the connection provided in one
            // that inhibits the closing operation
            return new UnclosableConnection(dbConn);
        }
    }
    
    /**
     * This implementation opens the connection to the
     * database once, while it always returns the same
     * instance for all subsequent requests.
     *
     * @return the same database connection instance
     *         that is kept open for multiple requests.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Connection getDbConnection() throws SQLException {
        // Starts a try-catch block
        try {
            // Gets the only instance of an unclosable
            // connection held by this provider
            return this.get();
        }
        // Intercept any exceptions during initialization
        catch (final ConcurrentException ex) {
            // Raises the cause again
            throw (SQLException) ex.getCause();
        }
    }
    
    /**
     * Gets the connection provider used internally by this wrapper.
     * 
     * @return the {@link DbConnectionProvider} used internally.
     */
    public final DbConnectionProvider getInnerProvider() {
        // Returns the reference to the internal provider
        return this.mInner;
    }

    /**
     * Lets to close the internal connection used by the provider.
     * <p>This method is useful for making sure to close the connection
     * used internally by the wrapper instance.</p><p>Anyway it's not strictly
     * necessary to use this method to close the internal connection, as this
     * operation is however carried out automatically when this instance of
     * this provider is collected by the Java Garbage Collector.</p>
     * 
     * @return {@code true} if the connection instance held by
     *         this provider was closed regularly, {@code false}
     *         if no connection was still required.
     * @throws SQLException if an error occurs while
     *         closing the internal connection.
     */
    public final boolean dispose() throws SQLException {
        // Check if the provider has been initialized
        if (this.mInit) {
            // Check if this object
            // still needs to be disposed
            if (!this.mDisposed) {
                // Starts a try-catch block
                try {
                    // Forcing the internal connection to shut down
                    this.get().closeInner();
                }
                // Intercept any exceptions during initialization
                catch (final ConcurrentException ex) {
                    // Raises the cause again
                    throw (SQLException) ex.getCause();
                }
                // Mark this object as disposed
                this.mDisposed = true;
            }
            // Indicates that the connection held
            // by this provider has been closed
            return true;
        }
        // Indicates that no connection
        // had yet been opened so far
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        // Check if the provider has been initialized
        if (this.mDisposed) {
            // Invokes the superclass method
            super.finalize();
        } else {
            // Starts a try-finally block
            try {
                // Forcing the internal
                // connection to shut down
                this.get().closeInner();
            } finally {
                // Mark this object as disposed
                this.mDisposed = true;
                // Invokes the superclass method
                super.finalize();
            }
        }
    }
    
    /**
     * Implementation of the {@link AutoCloseable}
     * interface that closes any connection held by
     * this provider (if it is initialized).
     * <p>This method is equivalent to {@link #dispose()},
     * except that the return value is ignored.</p>
     * 
     * @throws SQLException if an error occurs while
     *         closing the internal connection.
     */
    @Override
    public void close() throws SQLException {
        // Invokes the disposal procedure
        this.dispose();
    }
}
