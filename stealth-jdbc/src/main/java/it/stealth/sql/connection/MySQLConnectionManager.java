/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.connection;

import it.stealth.sql.util.SQLAccessViolationException;
import it.stealth.sql.util.SQLUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Implementation of the {@code DbConnectionManager}
 * interface specific for MySQL databases.
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public class MySQLConnectionManager implements DbConnectionProvider
{
    /**
     * Defines the connection port to the database server.
     */
    private short mPort;
    
    /**
     * Keeps ancillary properties required
     * for the database connection.
     */
    private final Properties mDbProps;
    
    /**
     * ##########################################.
     */
    private static final int ER_CANNOT_USER = 1396;
    
    /**
     * Defines the standard connection port to MySQL databases.
     */
    private static final short StandardPort = 3306;
    
    /**
     * Defines the parameter of the user name with which
     * the connection to the database is established.
     */
    private static final String UserParam = "user";
    
    /**
     * Indicates whether the JDBC driver for this database has already been loaded.
     */
    private static boolean DriverUnloadedFlag = true;
    
    /**
     * Key properties that can reach the database.
     */
    private String mDbUrl, mServerAddress, mDbSchema;
    
    /**
     * Defines the parameter of the password with which
     * the connection to the database is established.
     */
    private static final String PasswordParam = "password";
    
    /**
     * MySQL exception code indicating denial of access for a certain user.
     * 
     * @see https://dev.mysql.com/doc/refman/5.5/en/error-messages-server.html
     */
    private static final int ER_ACCESS_DENIED_ERROR = 1045;
    
    /**
     * MySQL exception code indicating that a certain
     * host is not allowed to connect with the database.
     */
    private static final int ER_HOST_NOT_PRIVILEGED = 1130;
    
    /**
     * MySQL exception code indicating the denial of
     * access for a certain user to a certain database.
     */
    private static final int ER_DBACCESS_DENIED_ERROR = 1044;
    
    /**
     * ##############################################################.
     */
    private static final int ER_SPECIFIC_ACCESS_DENIED_ERROR = 1227;
    
    /**
     * Defines the URL format for the database connection.
     */
    private static final String DbUrlFormat = "jdbc:mysql://%s:%d/%s";
    
    /**
     * MySQL exception code indicating the denial of access
     * for a certain user (regardless of the password used).
     */
    private static final int ER_ACCESS_DENIED_NO_PASSWORD_ERROR = 1698;
    
    /**
     * Defines the parameter in the database connection string that
     * specifies whether multiple queries can be made in a single statement.
     */
    private static final String AllowMultiQueriesParam = "allowMultiQueries";
    
    /**
     * Classpath related to the JDBC driver used to access the database.
     */
    private final static String JdbcDriverClasspath = "com.mysql.jdbc.Driver";
    
    /**
     * Creates a new instance of the {@link MySQLConnectionManager} class,
     * loading the JDBC driver if necessary.<p>The JDBC driver is only loaded
     * the first time this class is instantiated throughout the application.</p>
     * 
     * @throws SQLException if the JDBC driver is not found.
     */
    public MySQLConnectionManager() throws SQLException {
        // Load the JDBC driver for database connection
        MySQLConnectionManager.loadJdbcDriver();
        // Use the standard port for connecting to MySQL databases
        this.mPort = MySQLConnectionManager.StandardPort;
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
        if (MySQLConnectionManager.DriverUnloadedFlag) {
            // Load the JDBC driver for database connection
            SQLUtils.loadJdbcDriver(MySQLConnectionManager.JdbcDriverClasspath);
            // Mark the JDBC driver for this type of database as loaded
            MySQLConnectionManager.DriverUnloadedFlag = false;
        }
    }
    
    /**
     * Internal procedure used to intercept a database access violation.
     * 
     * @param sqlEx {@link SQLException} to be evaluated.
     * @throws SQLException a {@link SQLAccessViolationException} if
     *                      an access violation is recognized.
     */
    private static void detectAccessViolation(final SQLException sqlEx) throws SQLException {
        // Check the value of the vendor error code
        switch (sqlEx.getErrorCode()) {
            // ###############################################
            case MySQLConnectionManager.ER_CANNOT_USER:
            // ###############################################
            case MySQLConnectionManager.ER_HOST_NOT_PRIVILEGED:
            // ###############################################
            case MySQLConnectionManager.ER_ACCESS_DENIED_ERROR:
            // ###############################################
            case MySQLConnectionManager.ER_DBACCESS_DENIED_ERROR:
            // ###############################################    
            case MySQLConnectionManager.ER_SPECIFIC_ACCESS_DENIED_ERROR:
            // ###############################################
            case MySQLConnectionManager.ER_ACCESS_DENIED_NO_PASSWORD_ERROR:
                // Wrap the current exception in one that
                // reports a database access violation
                throw new SQLAccessViolationException(sqlEx);
        }
    }

    /**
     * Set in a single operation the parameters necessary
     * for the construction of the database connection URL.
     * 
     * @param dbServerAddress address of the database server.
     * @param dbSchema name of the database schema
     * @param port connection port to the database server.
     * @see #setServerAddress(String)
     * @see #setDbSchema(String)
     * @see #setServerPort(short)
     */
    public void setDbUrlParams(final String dbServerAddress, final String dbSchema, final short port) {
        // Compose the URL used to connect to the database
        this.mDbUrl = String.format(MySQLConnectionManager.DbUrlFormat,
                dbServerAddress, port, dbSchema);
        // Stores the database server address
        this.mServerAddress = dbServerAddress;
        // Stores the database schema
        this.mDbSchema = dbSchema;
        // Stores the database server port
        this.mPort = port;
    }
    
    /**
     * Set in a single operation the parameters necessary
     * for the construction of the database connection URL.
     * 
     * @param dbServerAddress address of the database server.
     * @param dbSchema name of the database schema
     * @see #setServerAddress(String)
     * @see #setDbSchema(String)
     */
    public final void setDbUrlParams(final String dbServerAddress, final String dbSchema) {
        // Overwrites the connection URL by changing the server address and database schema
        this.setDbUrlParams(dbServerAddress, dbSchema, this.mPort);
    }
    
    /**
     * Set the address of the database server.
     * 
     * @param dbServerAddress address of the database server.
     * @see #getServerAddress()
     */
    public final void setServerAddress(final String dbServerAddress) {
        // Overwrites the connection URL by changing the server address
        this.setDbUrlParams(dbServerAddress, this.mDbSchema, this.mPort);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Connection getDbConnection() throws SQLException {
        // Starts a try-catch block
        try {
            // Attempts to establish a connection to the database
            return DriverManager.getConnection(this.mDbUrl, this.mDbProps);
        }
        // Catch any SQL exception
        catch (final SQLException sqlEx) {
            // Invokes the procedure to intercept an access violation
            MySQLConnectionManager.detectAccessViolation(sqlEx);
            // Raise the previous exception
            throw sqlEx;
        }
    }
    
    /**
     * Set the value that allows to submit
     * multiple queries in a single statement.
     * 
     * @param value value indicating the ability to perform
     *              multiple queries in a single statement.
     * @see #isAllowMultiQueries()
     */
    public final void setAllowMultiQueries(final boolean value) {
        // Stores the value provided within the ancillary properties
        this.mDbProps.put(MySQLConnectionManager.
                AllowMultiQueriesParam, Boolean.
                toString(value));
    }
    
    /**
     * Set the database schema name.
     * 
     * @param dbSchema name of the database schema
     * @see #getDbSchema()
     */
    public final void setDbSchema(final String dbSchema) {
        // Overwrites the connection URL by changing the database schema
        this.setDbUrlParams(this.mServerAddress, dbSchema, this.mPort);
    }
    
    /**
     * Set the connection port to the database server.
     * 
     * @param dbServerPort connection port to the database server.
     * @see #getServerPort()
     */
    public void setServerPort(final short dbServerPort) {
        // Stores the connection port to the database
        this.mPort = dbServerPort;
    }
    
    /**
     * Returns the classpath of the JDBC driver used to connect to this database.
     * 
     * @return classpath of the JDBC driver used to connect to this database.
     */
    public final String getJdbcDriverClasspath() {
        // Returns the classpath of the JDBC driver
        return MySQLConnectionManager.JdbcDriverClasspath;
    }

    /**
     * Set the password with which the
     * database connection is established.
     * 
     * @param dbPassword password used to access the database.
     * @see #getDbPassword()
     */
    public void setDbPassword(final String dbPassword) {
        // Stores the value provided within the ancillary properties
        this.mDbProps.setProperty(MySQLConnectionManager.
                PasswordParam, dbPassword);
    }
    
    /**
     * Set the username with which the
     * database connection is established.
     * 
     * @param dbUsername username used to access the database.
     * @see #getDbUsername()
     */
    public void setDbUsername(final String dbUsername) {
        // Stores the value provided within the ancillary properties
        this.mDbProps.setProperty(MySQLConnectionManager.
                UserParam, dbUsername);
    }
    
    /**
     * Indicates whether multiple queries can
     * be submitted in a single statement.
     * 
     * @return value indicating the ability to perform
     *         multiple queries in a single statement.
     * @see #setAllowMultiQueries(boolean)
     */
    public boolean isAllowMultiQueries() {
        // Retrieves the value required by ancillary properties
        return Boolean.parseBoolean(this.mDbProps.getProperty(
                MySQLConnectionManager.AllowMultiQueriesParam));
    }
    
    /**
     * Get the database server address.
     * 
     * @return database server address.
     * @see #setServerAddress(String)
     */
    public String getServerAddress() {
        // Returns the stored value
        return this.mServerAddress;
    }

    /**
     * Get the password with which the
     * database connection is established.
     * 
     * @return password used to access the database.
     * @see #setDbPassword(String)
     */
    public String getDbPassword() {
        // Retrieves the value required by ancillary properties
        return this.mDbProps.getProperty(MySQLConnectionManager.PasswordParam);
    }

    /**
     * Get the username with which the
     * database connection is established.
     * 
     * @return username used to access the database.
     * @see #setDbUsername(String)
     */
    public String getDbUsername() {
        // Retrieves the value required by ancillary properties
        return this.mDbProps.getProperty(MySQLConnectionManager.UserParam);
    }

    /**
     * Get the connection port to the database server.
     * 
     * @return connection port to the database server.
     * @see #setServerPort(short)
     */
    public short getServerPort() {
        // Returns the stored value
        return this.mPort;
    }

    /**
     * Get the database schema name.
     * 
     * @return name of the database schema.
     * @see #setDbSchema(String)
     */
    public String getDbSchema() {
        // Returns the stored value
        return this.mDbSchema;
    }
}
