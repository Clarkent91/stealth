/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Objects;

/**
 * Implements a {@link DbConnectionProvider} wrapper able to keep alive
 * a {@link Connection}, or to inhibit the closing operation, if required.
 * <p>In detail, this class implements a simple switcher between a normal {@link
 * DbConnectionProvider} instance and an {@link UnclosableConnectionProvider} that
 * wraps the previous instance.</p><p>According to the specifications of {@link
 * UnclosableConnectionProvider}, in fact, each instance can have only one instance
 * that can not be renewed during its life cycle.</p><p>To implement a switch capable
 * of using different instances of {@link UnclosableConnectionProvider} it is necessary
 * a further layer provided by this implementation.</p><p>In this way it is possible to
 * enable or disable the function implemented by {@link UnclosableConnectionProvider}.</p>
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public class StickyConnectionProvider implements DbConnectionProvider
{
    /**
     * Original provider that provides the connection to the database.
     */
    private final DbConnectionProvider mOriginal;
    
    /**
     * Reference to the currently used database connection provider.
     * <p>This reference will be the same as the original provider
     * if the connection can be closed, otherwise it will be an
     * instance of {@link UnclosableConnectionProvider} if it
     * is required to keep a connection alive.</p>
     */
    private volatile DbConnectionProvider mCurrent;
    
    /**
     * Reference to an {@link UnclosableConnectionProvider} instance.
     * <p>If the current provider is the same as this instance, it means
     * that it has been requested to keep the connection alive, while if
     * it is the same as the orignial provider it means that the
     * connection provided can be closed.</p>
     */
    private volatile UnclosableConnectionProvider mUnclosable;
    
    /**
     * Exception message indicating that the previously open connection could not be closed.
     */
    private static final String ExUnableClosePreviousConnection = "Unable to close previous connection.";
    
    /**
     * Creates a new {@link StickyConnectionProvider} instance
     * initially set up to keep the supplied connection alive.
     * 
     * @param dbConnectionProvider original provider of the database connection.
     * @throws NullPointerException if {@code dbConnectionProvider} is {@code null}.
     */
    public StickyConnectionProvider(final DbConnectionProvider dbConnectionProvider) {
        // Stores the reference to the original connection provider
        this.mOriginal = Objects.requireNonNull(dbConnectionProvider);
        // Instantiates a provider that inhibits the operation of closing the connection
        this.mUnclosable = new UnclosableConnectionProvider(dbConnectionProvider);
        // Use the newly created provider as the current provider
        this.mCurrent = this.mUnclosable;
    }
    
    /**
     * Creates a new {@link StickyConnectionProvider} instance, specifying whether
     * to keep the supplied connection alive, or use the original provider directly.
     * 
     * @param dbConnectionProvider original provider of the database connection.
     * @param keepAlive {@code true} to keep alive the connection provided by this provider.
     * @throws NullPointerException if {@code dbConnectionProvider} is {@code null}.
     */
    public StickyConnectionProvider(final DbConnectionProvider dbConnectionProvider, final boolean keepAlive) {
        // Stores the reference to the original connection provider
        this.mOriginal = Objects.requireNonNull(dbConnectionProvider);
        // Check whether to keep the connection alive
        if (keepAlive) {
            // Instantiates a provider that inhibits the operation of closing the connection
            this.mUnclosable = new UnclosableConnectionProvider(dbConnectionProvider);
            // Use the newly created provider as the current provider
            this.mCurrent = this.mUnclosable;
        } else {
            // Use the original provider as the current provider
            this.mCurrent = this.mOriginal;
        }
    }
    
    /**
     * Set a value to keep the supplied connection alive.
     * <p>Keeping a connection alive means inhibiting the closing operation,
     * which normally allows terminating communication with the database.</p>
     * <p>If for some reason it is necessary to reuse the same connection on
     * several occasions (that is, multiple calls of the {@link #getDbConnection()}
     * method) and you want to inhibit the closing operation for all the duration
     * of this period, you can set the retention for the connection provided by
     * this provider.</p>
     * 
     * @param keepAlive {@code true} to keep alive the connection provided by this provider.
     * @throws SQLWarning if errors occur during the resetting of the closing operation.
     * @see #isDbConnectionKeptAlive()
     */
    public final synchronized void setDbConnectionKeepAlive(
            final boolean keepAlive) throws SQLWarning {
        // Check that the value provided is different from the previous one
        if (keepAlive ^ this.isDbConnectionKeptAlive()) {
            // Check whether to keep the connection alive
            if (keepAlive) {
                // Instantiates a provider that inhibits the operation of closing the connection
                this.mUnclosable = new UnclosableConnectionProvider(this.mOriginal);
                // Use the newly created provider as the current provider
                this.mCurrent = this.mUnclosable;
            } else {
                // Restore the original provider
                this.mCurrent = this.mOriginal;
                // Starts a try-catch-finnaly block
                try {
                    // Dispose of the previous provider
                    this.mUnclosable.dispose();
                }
                // Catch any SQL exception
                catch (final SQLException ex) {
                    // Raises a warning exception indicating that the
                    // previously open connection could not be closed
                    throw new SQLWarning(StickyConnectionProvider.
                            ExUnableClosePreviousConnection,
                            ex.getSQLState(), ex);
                }
                finally {
                    // Clean the reference to
                    // the previous provider
                    this.mUnclosable = null;
                }
            }
        }
    }
    
    /**
     * This implementation provides connections to which the
     * closing operation was inhibited, if it were required to keep
     * them alive, otherwise the instances provided by the original provider.
     * <p>In other words, if the {@link #isDbConnectionKeptAlive()} returns
     * {@code true}, this method will return connection instances to the
     * database to which the close operation was inhibited.</p>
     *
     * @return a connection to the database.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Connection getDbConnection() throws SQLException {
        // Return the connection provided by the current provider
        return this.mCurrent.getDbConnection();
    }
    
    /**
     * Gets the connection provider used internally by this wrapper.
     * 
     * @return the {@link DbConnectionProvider} used internally.
     */
    public final DbConnectionProvider getOriginalProvider() {
        // Returns the reference to the original provider
        return this.mOriginal;
    }
    
    /**
     * Indicates whether the connection provided by this provider is kept alive.
     * <p>Keeping a connection alive means inhibiting the closing operation,
     * which normally allows terminating communication with the database.</p>
     * 
     * @return {@code true} if the connection provided is kept alive.
     * @see #setDbConnectionKeepAlive(boolean)
     */
    public final boolean isDbConnectionKeptAlive() {
        // Check if the current provider is different from the original
        return this.mCurrent != this.mOriginal;
    }
}
