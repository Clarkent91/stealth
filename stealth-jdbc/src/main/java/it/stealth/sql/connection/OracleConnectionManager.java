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
 * Implementation of the {@code DbConnectionManager}
 * interface specific for Oracle databases.
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public final class OracleConnectionManager implements DbConnectionProvider
{
    /**
     * Defines the connection port to the database server.
     */
    private short mPort;
    
    /**
     * Keeps ancillary properties required for the database connection.
     */
    private final Properties mDbProps;
    
    /**
     * Key properties that can reach the database.
     */
    private String mDbUrl, mServerAddress, mSID;
    
    /**
     * Defines the parameter of the user name with which
     * the connection to the database is established.
     */
    private static final String UserParam = "user";

    /**
     * Defines the standard connection port to Oracle databases.
     */
    private static final short StandardPort = 1521;
    
    /**
     * Indicates whether the JDBC driver for this database has already been loaded.
     */
    private static boolean DriverUnloadedFlag = true;
    
    /**
     * Defines the parameter of the password with which
     * the connection to the database is established.
     */
    private static final String PasswordParam = "password";
    
    /**
     * Defines the URL format for the database connection.
     */
    private static final String DbUrlFormat = "jdbc:oracle:thin:@%s:%d:%s";
    
    /**
     * Classpath related to the JDBC driver used to access the database.
     */
    private final static String JdbcDriverClasspath = "oracle.jdbc.OracleDriver";
    
    /**
     * Creates a new instance of the {@link MySQLConnectionManager} class,
     * loading the JDBC driver if necessary.<p>The JDBC driver is only loaded
     * the first time this class is instantiated throughout the application.</p>
     * 
     * @throws SQLException if the JDBC driver is not found.
     */
    public OracleConnectionManager() throws SQLException {
        // Load the JDBC driver for database connection
        OracleConnectionManager.loadJdbcDriver();
        // Use the standard port for connecting to MySQL databases
        this.mPort = OracleConnectionManager.StandardPort;
        // Instantiates a set to hold connection properties
        this.mDbProps = new Properties();
    }
    
    /**
     * Load the JDBC driver for database connection.
     * 
     * @throws SQLException if the JDBC driver is not found.
     */
    private synchronized static void loadJdbcDriver() throws SQLException {
        // Check that the JDBC driver still needs to be loaded
        if (OracleConnectionManager.DriverUnloadedFlag) {
            // Load the JDBC driver for database connection
            SQLUtils.loadJdbcDriver(OracleConnectionManager.JdbcDriverClasspath);
            // Mark the JDBC driver for this type of database as loaded
            OracleConnectionManager.DriverUnloadedFlag = false;
        }
    }
    
    /**
     * Set in a single operation the parameters necessary
     * for the construction of the database connection URL.
     * 
     * @param serverAddress address of the database server.
     * @param sid Oracle System ID that identifies the database.
     * @see #setServerAddress(String)
     * @see #setSID(String)
     */
    public final void setDbUrlParams(final String serverAddress, final String sid) {
        // Overwrites the connection URL by changing the server address and the SID
        this.setDbUrlParams(serverAddress, sid, this.mPort);
    }
    
    /**
     * Set in a single operation the parameters necessary
     * for the construction of the database connection URL.
     * 
     * @param serverAddress address of the database server.
     * @param sid Oracle System ID that identifies the database.
     * @param port connection port to the database server.
     * @see #setServerAddress(String)
     * @see #setServerPort(short)
     * @see #setSID(String)
     */
    public void setDbUrlParams(final String serverAddress,
            final String sid, final short port) {
        // Compose the URL used to connect to the database
        this.mDbUrl = String.format(OracleConnectionManager.
                DbUrlFormat,serverAddress, port, sid);
        // Stores the database server address
        this.mServerAddress = serverAddress;
        // Stores the database server port
        this.mPort = port;
        // Stores the database SID
        this.mSID = sid;
    }
    
    /**
     * Set the address of the database server.
     * 
     * @param serverAddress address of the database server.
     * @see #getServerAddress()
     */
    public final void setServerAddress(final String serverAddress) {
        // Overwrites the connection URL by changing the server address
        this.setDbUrlParams(serverAddress, this.mSID, this.mPort);
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
     * Set the connection port to the database server.
     * 
     * @param serverPort connection port to the database server.
     * @see #getServerPort()
     */
    public void setServerPort(final short serverPort) {
        // Overwrites the connection URL by changing the server address
        this.setDbUrlParams(this.mServerAddress, this.mSID, serverPort);
    }
    
    /**
     * Returns the classpath of the JDBC driver used to connect to this database.
     * 
     * @return classpath of the JDBC driver used to connect to this database.
     */
    public final String getJdbcDriverClasspath() {
        // Returns the classpath of the JDBC driver
        return OracleConnectionManager.JdbcDriverClasspath;
    }
    
    /**
     * Set the Oracle System ID that identifies the database.
     * 
     * @param sid Oracle System ID that identifies the database.
     * @see #getSID()
     */
    public final void setSID(final String sid) {
        // Overwrites the connection URL by changing the SID
        this.setDbUrlParams(this.mServerAddress, sid, this.mPort);
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
        this.mDbProps.setProperty(OracleConnectionManager.
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
        this.mDbProps.setProperty(OracleConnectionManager.
                UserParam, dbUsername);
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
        return this.mDbProps.getProperty(OracleConnectionManager.PasswordParam);
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
        return this.mDbProps.getProperty(OracleConnectionManager.UserParam);
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
     * Get the Oracle System ID that identifies the database.
     * 
     * @return Oracle System ID that identifies the database.
     * @see #setSID(String)
     */
    public String getSID() {
        // Returns the stored value
        return this.mSID;
    }
}
