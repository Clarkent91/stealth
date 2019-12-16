/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface that provides an open connection to the database.
 * <p>Depending on the implementation provided, it can provide a
 * different connection or the same connection every time.</p>
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public interface DbConnectionProvider
{
    /**
     * Attempts to establish a connection to the database.
     * <p>The canonical implementation makes use of the
     * {@link java.sql.DriverManager} class to provide
     * the connection to the database.</p>
     * 
     * @return a connection to the database.
     * @exception SQLException if a database access error occurs.
     * @exception java.sql.SQLTimeoutException  when the driver has determined
     * that the timeout value specified by the {@code setLoginTimeout} method
     * has been exceeded and has at least tried to cancel the
     * current database connection attempt.
     */
    public Connection getDbConnection() throws SQLException;
}
