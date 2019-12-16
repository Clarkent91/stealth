/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.connection;

import it.stealth.sql.util.SQLUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author fabrizio
 */
public class SQLiteConnectionManager implements DbConnectionProvider
{
    /**
     * ######################
     */
    private String mDbUrl, mDbPath;
    
    /**
     * Keeps ancillary properties required
     * for the database connection.
     */
    private final Properties mDbProps;
    
    /**
     * Indicates whether the JDBC driver for
     * this database has already been loaded.
     */
    private static boolean DriverUnloadedFlag = true;
    
    /**
     * Defines the URL format for the database connection.
     */
    private static final String DbUrlFormat = "jdbc:sqlite:";
    
    /**
     * Classpath related to the JDBC driver used to access the database.
     */
    private final static String JdbcDriverClasspath = "org.sqlite.JDBC";

    /**
     * Creates a new instance of the {@link SQLiteConnectionManager} class,
     * loading the JDBC driver if necessary.<p>The JDBC driver is only loaded
     * the first time this class is instantiated throughout the application.</p>
     * 
     * @throws SQLException if the JDBC driver is not found.
     */
    public SQLiteConnectionManager() throws SQLException {
        // Load the JDBC driver for database connection
        SQLiteConnectionManager.loadJdbcDriver();
        // #############################################
        this.mDbProps = new Properties();
    }
    
    /**
     * Load the JDBC driver for database connection.
     * 
     * @throws SQLException if the JDBC driver is not found.
     */
    private synchronized static void loadJdbcDriver() throws SQLException {
        // Check that the JDBC driver still needs to be loaded
        if (SQLiteConnectionManager.DriverUnloadedFlag) {
            // Load the JDBC driver for database connection
            SQLUtils.loadJdbcDriver(SQLiteConnectionManager.JdbcDriverClasspath);
            // Mark the JDBC driver for this type of database as loaded
            SQLiteConnectionManager.DriverUnloadedFlag = false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Connection getDbConnection() throws SQLException {
        // Attempts to establish a connection to the database
        return DriverManager.getConnection(this.mDbUrl, this.mDbProps);
    }
    
    /**
     * #########################################.
     * 
     * @param dbPath ##########################.
     */
    public final void setDbPath(final String dbPath) {
        // Compose the URL used to connect to the database
        this.mDbUrl = SQLiteConnectionManager.DbUrlFormat.concat(dbPath);
        // Stores the database path
        this.mDbPath = dbPath;
    }
    
    /**
     * Returns the classpath of the JDBC driver used to connect to this database.
     * 
     * @return classpath of the JDBC driver used to connect to this database.
     */
    public final String getJdbcDriverClasspath() {
        // Returns the classpath of the JDBC driver
        return SQLiteConnectionManager.JdbcDriverClasspath;
    }
    
    /**
     * ################################.
     * 
     * @return #######################.
     */
    public final String getDbPath() {
        // ###########################
        return this.mDbPath;
    }
}
