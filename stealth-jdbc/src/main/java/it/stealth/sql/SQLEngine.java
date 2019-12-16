/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql;

import it.stealth.sql.connection.DbConnectionProvider;
import it.stealth.sql.connection.StickyConnectionProvider;
import it.stealth.sql.connection.UnclosableConnectionProvider;
import it.stealth.sql.readers.SQLQueryReader;
import it.stealth.sql.readers.SQLQueryReaders;
import it.stealth.sql.wrappers.PreparedStatementWrapper;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import org.apache.commons.lang3.Validate;
import it.stealth.sql.util.SQLUtils;

/**
 * Defines an object responsible for executing SQL commands.
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public class SQLEngine
{
    /**
     * Reference to the internal connection provider.
     */
    private final StickyConnectionProvider mProvider;
    
    /**
     * Defines the parameter name for the JDBC connection.
     */
    protected static final String DbConnParam = "dbConn";
    
    /**
     * Defines the parameter name for an SQL command.
     */
    private static final String SqlCommandParam = "sqlCommand";
    
    /**
     * Defines the parameter name for the query result reader.
     */
    private static final String QueryReaderParam = "queryReader";
    
    /**
     * Defines the name of the parameter related to the database connection provider.
     */
    private static final String DbConnProviderParam = "dbConnectionProvider";
    
    /**
     * Exception message raised when the supplied SQL
     * command string is {@code null} or an blank string.
     */
    private static final String ExBlankSqlCmd = "Unable to generate a "
            + "SQL statement starting from a null or blank string.";
    
    /**
     * Initializes an instance of {@link SQLEngine}
     * class specifying the database connection provider.
     * 
     * @param dbConnectionProvider database connection provider.
     * @throws NullPointerException if {@code dbConnectionProvider} is {@code null}.
     */
    public SQLEngine(final DbConnectionProvider dbConnectionProvider) {
        // Invokes the overloaded constructor
        this(dbConnectionProvider, false);
    }
    
    /**
     * Initializes an instance of {@link SQLEngine} class
     * specifying the database connection provider and whether to
     * keep the database connection alive for multiple requests.
     * 
     * @param dbConnectionProvider database connection provider.
     * @param keepAlive {@code true} to keep alive the connection provided.
     * @throws NullPointerException if {@code dbConnectionProvider} is {@code null}.
     */
    public SQLEngine(final DbConnectionProvider dbConnectionProvider, final boolean keepAlive) {
        // Check that the database connection provider is not null and install it within an object
        // that implements the functionality to keep the database connection alive on demand
        this.mProvider = new StickyConnectionProvider(SQLEngine.unwrap(
                Objects.requireNonNull(dbConnectionProvider, SQLEngine.
                DbConnProviderParam)), keepAlive);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PreparedStatement wrapper that closes all associated resources">
    /**
     * Implements a {@link PreparedStatement} wrapper tasked with closing all
     * resources associated with that statement during the closing phase.
     */
    private static final class RHPS extends PreparedStatementWrapper
    {
        /**
         * Reference to the object queue to be closed.
         */
        private final Queue<AutoCloseable> mQueue;
        
        /**
         * Class constructor.
         *
         * @param inner statement to wrap.
         */
        public RHPS(final PreparedStatement inner) {
            // Invokes the superclass builder
            super(inner);
            // Instantiate the queue of associated resources
            this.mQueue = new LinkedList<>();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setClob(final int parameterIndex,
                final Reader reader) throws SQLException {
            // Invokes the superclass method
            super.setClob(parameterIndex, reader);
            // Add the parameter to the queue
            this.mQueue.add(reader);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setNClob(final int parameterIndex,
                final Reader reader) throws SQLException {
            // Invokes the superclass method
            super.setNClob(parameterIndex, reader);
            // Add the parameter to the queue
            this.mQueue.add(reader);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setAsciiStream(final int parameterIndex,
                final InputStream x) throws SQLException {
            // Invokes the superclass method
            super.setAsciiStream(parameterIndex, x);
            // Add the parameter to the queue
            this.mQueue.add(x);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setBinaryStream(final int parameterIndex,
                final InputStream x) throws SQLException {
            // Invokes the superclass method
            super.setBinaryStream(parameterIndex, x);
            // Add the parameter to the queue
            this.mQueue.add(x);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setBlob(final int parameterIndex, final
                InputStream inputStream) throws SQLException {
            // Invokes the superclass method
            super.setBlob(parameterIndex, inputStream);
            // Add the parameter to the queue
            this.mQueue.add(inputStream);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setCharacterStream(final int parameterIndex,
                final Reader reader) throws SQLException {
            // Invokes the superclass method
            super.setCharacterStream(parameterIndex, reader);
            // Add the parameter to the queue
            this.mQueue.add(reader);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setNCharacterStream(final int parameterIndex,
                final Reader value) throws SQLException {
            // Invokes the superclass method
            super.setNCharacterStream(parameterIndex, value);
            // Add the parameter to the queue
            this.mQueue.add(value);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setClob(final int parameterIndex, final Reader
                reader, final long length) throws SQLException {
            // Invokes the superclass method
            super.setClob(parameterIndex, reader, length);
            // Add the parameter to the queue
            this.mQueue.add(reader);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setNClob(final int parameterIndex, final Reader
                reader, final long length) throws SQLException {
            // Invokes the superclass method
            super.setNClob(parameterIndex, reader, length);
            // Add the parameter to the queue
            this.mQueue.add(reader);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setAsciiStream(final int parameterIndex, final
                InputStream x, final int length) throws SQLException {
            // Invokes the superclass method
            super.setAsciiStream(parameterIndex, x, length);
            // Add the parameter to the queue
            this.mQueue.add(x);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setBinaryStream(final int parameterIndex, final
                InputStream x, final int length) throws SQLException {
            // Invokes the superclass method
            super.setBinaryStream(parameterIndex, x, length);
            // Add the parameter to the queue
            this.mQueue.add(x);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setAsciiStream(final int parameterIndex, final
                InputStream x, final long length) throws SQLException {
            // Invokes the superclass method
            super.setAsciiStream(parameterIndex, x, length);
            // Add the parameter to the queue
            this.mQueue.add(x);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setBinaryStream(final int parameterIndex, final
                InputStream x, final long length) throws SQLException {
            // Invokes the superclass method
            super.setAsciiStream(parameterIndex, x, length);
            // Add the parameter to the queue
            this.mQueue.add(x);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setUnicodeStream(final int parameterIndex, final
                InputStream x, final int length) throws SQLException {
            // Invokes the superclass method
            super.setUnicodeStream(parameterIndex, x, length);
            // Add the parameter to the queue
            this.mQueue.add(x);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setBlob(final int parameterIndex, final InputStream
                inputStream, final long length) throws SQLException {
            // Invokes the superclass method
            super.setBlob(parameterIndex, inputStream, length);
            // Add the parameter to the queue
            this.mQueue.add(inputStream);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setCharacterStream(final int parameterIndex, final
                Reader reader, final int length) throws SQLException {
            // Invokes the superclass method
            super.setCharacterStream(parameterIndex, reader, length);
            // Add the parameter to the queue
            this.mQueue.add(reader);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setCharacterStream(final int parameterIndex, final
                Reader reader, final long length) throws SQLException {
            // Invokes the superclass method
            super.setCharacterStream(parameterIndex, reader, length);
            // Add the parameter to the queue
            this.mQueue.add(reader);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setNCharacterStream(final int parameterIndex, final
                Reader value, final long length) throws SQLException {
            // Invokes the superclass method
            super.setNCharacterStream(parameterIndex, value, length);
            // Add the parameter to the queue
            this.mQueue.add(value);
        }
        
        /**
         * Closes all resources associated with the statement.
         *
         * @throws SQLException if an error occurs while closing a resource.
         */
        private void closeResources() throws SQLException {
            // Declare a support reference
            AutoCloseable res;
            // Itera until the queue is empty
            while (!this.mQueue.isEmpty()) {
                // Dequeue a resource from queue
                res = this.mQueue.poll();
                // Check that this resource is not null
                if (res != null) {
                    // Starts a try-catch block
                    try {
                        // Closes this resource
                        res.close();
                    }
                    // Capture any exception
                    // raised when closing a resource
                    catch (final Exception ex) {
                        // Wrap this exception in a SQLException
                        throw new SQLException(ex);
                    }
                }
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void clearParameters() throws SQLException {
            // Invokes the superclass method
            super.clearParameters();
            // Close all associated resources
            this.closeResources();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws SQLException {
            // Invokes the superclass method
            super.close();
            // Close all associated resources
            this.closeResources();
        }
    }//</editor-fold>
    
    /**
     * Envelops the statement in an instance that closes all resources in closing.
     * <p>By wrapping a {@link PreparedStatement} through this method it is ensured
     * that all the resources associated with it (each object implements the
     * {@link AutoCloseable} interface, such as {@link InputStream} and
     * {@link Reader}) are released when the statement is closed.</p>
     *
     * @param stmt a {@link PreparedStatement} to wrap.
     * @return an instance of {@link PreparedStatement} that closes all resources being closed.
     */                                      
    public static final PreparedStatement asResourceHolder(final PreparedStatement stmt) {
        // Envelops the statement in an instance that closes all resources in closing
        return stmt instanceof SQLEngine.RHPS ? stmt : new SQLEngine.RHPS(stmt);
    }
    
    /**
     * Internal procedure that attempts to recover the original
     * {@link DbConnectionProvider}, freeing it from external layers.
     * <p>Among the known external layers are the classes: {@link
     * SQLEngine}, {@link StickyConnectionProvider} and {@link
     * UnclosableConnectionProvider}.</p>
     * 
     * @param dcp {@link DbConnectionProvider} reference to unwrap.
     * @return internal instance managed by the classes listed in
     *         the description, or the instance itself.
     */
    private static DbConnectionProvider unwrap(final DbConnectionProvider dcp) {
        // Check if a StickyConnectionProvider has been provided
        if (dcp instanceof StickyConnectionProvider) {
            // Retrieves the original connection provider reference
            return ((StickyConnectionProvider) dcp).getOriginalProvider();
        }
        // Check if a UnclosableConnectionProvider has been provided
        if (dcp instanceof UnclosableConnectionProvider) {
            // Retrieves the original connection provider reference
            return ((UnclosableConnectionProvider) dcp).getInnerProvider();
        }
        // Return the instance provided
        return dcp;
    }
    
    /**
     * Executes an SQL query by storing the results in a {@link QueryResults}.
     * <p>This procedure is designed to perform simple (non-parametric) queries.</p>
     * <p>The query results are stored and returned into a {@link QueryResults}. It
     * can be considered the disconnected version of a {@link ResultSet}, since it
     * does not hold any connection resource to the data source with which it was
     * populated.</p>
     * 
     * @param sqlQuery SQL query that must be sent to the database.
     * @return {@link QueryResults} that encapsulates the query results.
     * @throws SQLException if an error occurs while querying the database.
     * @see QueryResults
     */
    public final QueryResults query(final String sqlQuery) throws SQLException {
        // Executes the query provided by storing the results in a QueryResults
        return this.query(sqlQuery, SQLQueryReaders.DefaultReader);
    }
    
    /**
     * Executes an SQL query whose results are read by a certain reader.
     * <p>This procedure is designed to perform simple (non-parametric) queries.</p>
     * <p>The control of the {@link ResultSet} is passed directly to the reader,
     * who has the task of scrolling through the records provided by it.</p>
     * 
     * @param <T> type of the object returned by the query.
     * @param sqlQuery SQL query that must be sent to the database.
     * @param queryReader reader of the query results.
     * @return object that encapsulates the query results.
     * @throws SQLException if an error occurs while querying the database.
     */
    public final <T> T query(final String sqlQuery, final
            SQLQueryReader<T> queryReader) throws SQLException {
        // Check that the query reader reference is not null
        Objects.requireNonNull(queryReader, SQLEngine.QueryReaderParam);
        // Make sure the SQL query string is not a null or blank string
        Validate.notBlank(sqlQuery, SQLEngine.ExBlankSqlCmd);
        // Opens a connection to the database in which to perform the query
        try (final Connection dbConn = this.mProvider.getDbConnection()) {
            // Create an empty statement to send commands to the database
            try (final Statement stmt = dbConn.createStatement()) {
                // Execute the query and return the results
                try (final ResultSet rs = stmt.executeQuery(sqlQuery)) {
                    // Invokes the callback to read the query results
                    return queryReader.read(rs);
                }
            }
        }
    }
    
    /**
     * Executes an SQL query verifying if it returns at least one record.
     * <p>This procedure is designed to perform simple (non-parametric) queries.</p>
     * <p>This procedure does nothing more than execute the query provided, limiting
     * itself to determining whether the product {@link ResultSet} is not empty.</p>
     * 
     * @param sqlQuery SQL query that must be sent to the database.
     * @return {@code true} if the query produced at least one result.
     * @throws SQLException if an error occurs while querying the database.
     * @since 1.3
     */
    public final boolean hasRecord(final String sqlQuery) throws SQLException {
        // Executes the SQL query checking that at least one result is provided
        return this.query(sqlQuery, SQLUtils.HasQueryResults);
    }
    
//    /**
//     * Executes an SQL query whose results are read by a certain reader.
//     * <p>This procedure is designed to perform simple (non-parametric) queries.</p>
//     * <p>The control of the {@link ResultSet} is passed directly to the reader,
//     * who has the task of scrolling through the records provided by it.</p>
//     * 
//     * @param <T> type of the object returned by the query.
//     * @param sqlQuery SQL query that must be sent to the database.
//     * @param queryReader reader of the query results.
//     * @param args ####################################.
//     * @return object that encapsulates the query results.
//     * @throws SQLException if an error occurs while querying the database.
//     */
//    public final <T> T query(final String sqlQuery, final SQLQueryReader<T>
//            queryReader, final Object... args) throws SQLException {
//        // Check that the query reader reference is not null
//        Objects.requireNonNull(queryReader, SQLEngine.QueryReaderParam);
//        // Make sure the SQL query string is not a null or blank string
//        Validate.notBlank(sqlQuery, SQLEngine.ExBlankSqlCmd);
//        // Opens a connection to the database in which to perform the query
//        try (final Connection dbConn = this.mProvider.getDbConnection()) {
//            // Create an empty statement to send commands to the database
//            try (final PreparedStatement stmt = dbConn.prepareStatement(sqlQuery)) {
//                // ##################################
//                SQLEngine.writeParams(stmt, args);
//                // Execute the query and return the results
//                try (final ResultSet rs = stmt.executeQuery()) {
//                    // Invokes the callback to read the query results
//                    return queryReader.read(rs);
//                }
//            }
//        }
//    }
//    
//    private static void writeParams(final PreparedStatement
//            stmt, final Object[] args) throws SQLException {
//        int i;
//        if (args != null) {
//            for (i = 1; i < args.length; i++) {
//                stmt.setObject(i, args[i]);
//            }
//        }
//    }
    
    /**
     * Executes an SQL query by storing the results in a {@link QueryResults}.
     * <p>This procedure is designed to perform simple (non-parametric) queries.</p>
     * <p>The query results are stored and returned into a {@link QueryResults}. It
     * can be considered the disconnected version of a {@link ResultSet}, since it
     * does not hold any connection resource to the data source with which it was
     * populated.</p><p>This version uses the connection provided by the outside
     * to execute the query.</p>
     * 
     * @param dbConn open connection with the database.
     * @param sqlQuery SQL query that must be sent to the database.
     * @return {@link QueryResults} that encapsulates the query results.
     * @throws SQLException if an error occurs while querying the database.
     */
    public static final QueryResults query(final Connection
            dbConn, final String sqlQuery) throws SQLException {
        // Executes the query provided by storing the results in a QueryResults
        return SQLEngine.query(dbConn, sqlQuery, SQLQueryReaders.DefaultReader);
    }
    
    /**
     * Executes an SQL query whose results are read by a certain reader.
     * <p>This procedure is designed to perform simple (non-parametric) queries.</p>
     * <p>The control of the {@link ResultSet} is passed directly to the reader,
     * who has the task of scrolling through the records provided by it.</p>
     * <p>This version uses the connection provided by the outside to
     * execute the query.</p>
     * 
     * @param <T> type of the object returned by the query.
     * @param dbConn open connection with the database.
     * @param sqlQuery SQL query that must be sent to the database.
     * @param queryReader reader of the query results.
     * @return object that encapsulates the query results.
     * @throws SQLException if an error occurs while querying the database.
     */
    public static final <T> T query(final Connection dbConn, final String
            sqlQuery, final SQLQueryReader<T> queryReader) throws SQLException {
        // Check that the connection reference is not null
        Objects.requireNonNull(dbConn, SQLEngine.DbConnParam);
        // Check that the query reader reference is not null
        Objects.requireNonNull(queryReader, SQLEngine.QueryReaderParam);
        // Make sure the SQL query string is not a null or blank string
        Validate.notBlank(sqlQuery, SQLEngine.ExBlankSqlCmd);
        // Create an empty statement to send commands to the database
        try (final Statement stmt = dbConn.createStatement()) {
            // Execute the query and return the results
            try (final ResultSet rs = stmt.executeQuery(sqlQuery)) {
                // Invokes the callback to read the query results
                return queryReader.read(rs);
            }
        }
    }
    
    /**
     * Executes an SQL query verifying if it returns at least one record.
     * <p>This procedure is designed to perform simple (non-parametric) queries.</p>
     * <p>This procedure does nothing more than execute the query provided, limiting
     * itself to determining whether the product {@link ResultSet} is not empty.</p>
     * <p>This version uses the connection provided by the outside to
     * execute the query.</p>
     * 
     * @param dbConn open connection with the database.
     * @param sqlQuery SQL query that must be sent to the database.
     * @return {@code true} if the query produced at least one result.
     * @throws SQLException if an error occurs while querying the database.
     * @since 1.3
     */
    public static final boolean hasRecord(final Connection
            dbConn, final String sqlQuery) throws SQLException {
        // Executes the SQL query checking that at least one result is provided
        return SQLEngine.query(dbConn, sqlQuery, SQLUtils.HasQueryResults);
    }
    
    /**
     * Execute an SQL Data Manipulation Language command (ie INSERT,
     * UPDATE, REPLACE or DELETE) through the default connection
     * provided by the underlying {@link DbConnectionProvider}.
     * <p>It's also possible to request that the operation be
     * performed within an SQL transaction, through the
     * {@code transactional} parameter.</p>
     * 
     * @param sqlCommand SQL DML command to execute.
     * @param transactional indicates whether the command should
     *                      be executed within an SQL transaction.
     * @return either (1) the row count for SQL Data Manipulation Language
     *         statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException if a database access error occurs.
     */
    public final int execute(final String sqlCommand, final
            boolean transactional) throws SQLException {
        // Check whether to perform a transaction
        if (transactional) {
            // Execute the command within an SQL transaction
            return this.executeTran(sqlCommand);
        } else {
            // Execute the command with a normal connection
            return this.execute(sqlCommand);
        }
    }
    
    /**
     * Execute an SQL Data Manipulation Language command (ie INSERT,
     * UPDATE, REPLACE or DELETE) using a certain database connection.
     * 
     * @param dbConn open connection with the database.
     * @param sqlCommand SQL DML command to execute.
     * @return either (1) the row count for SQL Data Manipulation Language
     *         statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException if a database access error occurs.
     */
    public final static int execute(final Connection dbConn,
            final String sqlCommand) throws SQLException {
        // Make sure the SQL command string is not a null or blank string
        Validate.notBlank(sqlCommand, SQLEngine.ExBlankSqlCmd);
        // Create an empty statement to send commands to the database
        try (final Statement stmt = dbConn.createStatement()) {
            // Executes the SQL command provided
            return stmt.executeUpdate(sqlCommand);
        }
    }
    
    /**
     * Execute an SQL Data Manipulation Language command (ie INSERT,
     * UPDATE, REPLACE or DELETE) through the default connection
     * provided by the underlying {@link DbConnectionProvider}.
     * 
     * @param sqlCommand SQL DML command to execute.
     * @return either (1) the row count for SQL Data Manipulation Language
     *         statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException if a database access error occurs.
     */
    public final int execute(final String sqlCommand) throws SQLException {
        // Make sure the SQL command string is not a null or blank string
        Validate.notBlank(sqlCommand, SQLEngine.ExBlankSqlCmd);
        // Open a connection to the database on which to execute the command
        try (final Connection dbConn = this.getDbConnection()) {
            // Create an empty statement to send commands to the database
            try (final Statement stmt = dbConn.createStatement()) {
                // Executes the SQL command provided
                return stmt.executeUpdate(sqlCommand);
            }
        }
    }
    
    /**
     * Execute an SQL Data Manipulation Language command (ie
     * INSERT, UPDATE, REPLACE or DELETE) through a transaction
     * with the database that uses the connection provided by the
     * underlying {@link DbConnectionProvider}.
     * 
     * @param sqlCommand SQL DML command to execute.
     * @return either (1) the row count for SQL Data Manipulation Language
     *         statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException if a database access error occurs.
     */
    public final int executeTran(final String sqlCommand) throws SQLException {
        // Declares a support int
        final int rowCount;
        // Make sure the SQL command string is not a null or blank string
        Validate.notBlank(sqlCommand, SQLEngine.ExBlankSqlCmd);
        // Opens a transaction to the database to execute the command
        try (final Transaction dbTran = this.getDbTransaction()) {
            // Create an empty statement to send commands to the database
            try (final Statement stmt = dbTran.createStatement()) {
                // Executes the SQL command provided
                rowCount = stmt.executeUpdate(sqlCommand);
                // Mark this transaction as completed
                if (rowCount >= 0) dbTran.complete();
            }
        }
        // Returns the number of rows
        // affected by this command
        return rowCount;
    }
    
    /**
     * Execute a sequence of SQL Data Manipulation Language command
     * (ie INSERT, UPDATE, REPLACE or DELETE) through a transaction
     * with the database that uses the connection provided by the
     * underlying {@link DbConnectionProvider}.
     * 
     * @param sqlCommands array of SQL DML commands to execute.
     * @return either (1) the row count for SQL Data Manipulation Language
     *         statements or (2) 0 for SQL statements that return nothing.
     * @throws SQLException if a database access error occurs.
     */
    public final int executeTran(final String... sqlCommands) throws SQLException {
        // Declares three support integers
        int i, rowCount, totCount = 0;
        // Make sure that every SQL command is not null or blank
        SQLEngine.checkSqlCommandArray(sqlCommands);
        // Opens a transaction to the database to execute the command
        try (final Transaction dbTran = this.getDbTransaction()) {
            // Create an empty statement to send commands to the database
            try (final Statement stmt = dbTran.createStatement()) {
                // Iterate on all the SQL commands provided
                for (i = 0; i < sqlCommands.length; i++) {
                    // Executes each SQL command provided
                    rowCount = stmt.executeUpdate(sqlCommands[i]);
                    // Returns the counter value if negative
                    if (rowCount < 0) return rowCount;
                    // Adds this value to the total counter
                    totCount += rowCount;
                }
                // Check that at least one rows has been involved
                if (totCount > 0) {
                    // Mark this transaction as completed
                    dbTran.complete();
                }
            }
        }
        // Returns the total number of
        // rows involved by the commands
        return totCount;
    }
    
    /**
     * Executes a sequence of SQL Data Manipulation Language command (ie
     * INSERT, UPDATE, REPLACE or DELETE) through the default connection
     * provided by the underlying {@link DbConnectionProvider}.
     * 
     * @param sqlCommands array of SQL DML commands to execute.
     * @return sum of all the rows involved by the commands provided.
     * @throws SQLException if a database access error occurs.
     */
    public final int execute(final String... sqlCommands) throws SQLException {
        // Declares three support integers
        int i, rowCount, totCount = 0;
        // Make sure that every SQL command is not null or blank
        SQLEngine.checkSqlCommandArray(sqlCommands);
        // Open a connection to the database on which to execute the command
        try (final Connection dbConn = this.getDbConnection()) {
            // Create an empty statement to send commands to the database
            try (final Statement stmt = dbConn.createStatement()) {
                // Iterate on all the SQL commands provided
                for (i = 0; i < sqlCommands.length; i++) {
                    // Executes each SQL command provided
                    rowCount = stmt.executeUpdate(sqlCommands[i]);
                    // Returns the counter value if negative
                    if (rowCount < 0) return rowCount;
                    // Adds this value to the total counter
                    totCount += rowCount;
                }
            }
        }
        // Returns the total number of
        // rows involved by the commands
        return totCount;
    }
    
    /**
     * Executes a sequence of SQL Data Manipulation Language command (ie
     * INSERT, UPDATE, REPLACE or DELETE) using a certain database connection.
     * 
     * @param dbConn open connection with the database.
     * @param sqlCommands array of SQL DML commands to execute.
     * @return sum of all the rows involved by the commands provided.
     * @throws SQLException if a database access error occurs.
     */
    public final static int execute(final Connection dbConn,
            final String... sqlCommands) throws SQLException {
        // Declares three support integers
        int i, rowCount, totCount = 0;
        // Make sure that every SQL command is not null or blank
        SQLEngine.checkSqlCommandArray(sqlCommands);
        // Check that the connection reference is not null
        Objects.requireNonNull(dbConn, SQLEngine.DbConnParam);
        // Create an empty statement to send commands to the database
        try (final Statement stmt = dbConn.createStatement()) {
            // Iterate on all the SQL commands provided
            for (i = 0; i < sqlCommands.length; i++) {
                // Executes each SQL command provided
                rowCount = stmt.executeUpdate(sqlCommands[i]);
                // Returns the counter value if negative
                if (rowCount < 0) return rowCount;
                // Adds this value to the total counter
                totCount += rowCount;
            }
        }
        // Returns the total number of
        // rows involved by the commands
        return totCount;
    }
    
    /**
     * Internal utility method that checks
     * that no SQL command is null or blank.
     * 
     * @param sqlCommands array of SQL commands to check.
     */
    private static void checkSqlCommandArray(final String[] sqlCommands) {
        // Check that the array is not null or empty
        Validate.notEmpty(sqlCommands, SQLEngine.SqlCommandParam);
        // Iterate over all SQL commands held by the array
        for (final String sqlCommand : sqlCommands) {
            // Check that none of them is null or blank
            Validate.notBlank(sqlCommand, SQLEngine.SqlCommandParam);
        }
    }
    
    /**
     * Set a value that indicates whether the database
     * connection provided by this engine should be kept alive.
     * <p>Keeping a connection alive means inhibiting the closing operation,
     * which normally allows terminating communication with the database.</p>
     * <p>If for some reason it is necessary to reuse the same connection on
     * several occasions (that is, multiple calls of the {@link #getDbConnection()}
     * method) and you want to inhibit the closing operation for all the duration
     * of this period, you can set the retention for the connection provided by
     * this engine.</p>
     * 
     * @param keepAlive {@code true} to keep alive the connection provided by this engine.
     * @throws SQLWarning if errors occur during the resetting of the closing operation.
     * @see #isConnectionKeptAlive()
     */
    public final void setConnectionKeepAlive(final boolean keepAlive) throws SQLWarning {
        // Invokes the internal connection provider method
        this.mProvider.setDbConnectionKeepAlive(keepAlive);
    }
    
    /**
     * Attempts to establish a connection to
     * the database by starting a new transaction.
     * 
     * @return a connection to the database in the form of a transaction.
     * @throws SQLException if a database access error occurs.
     * @see it.stealth.sql.connection.DbConnectionProvider#getDbConnection()
     */
    public final Transaction getDbTransaction() throws SQLException {
        // Wraps the new connection within a transaction
        return new Transaction(this.mProvider.getDbConnection());
    }
    
    /**
     * Gets the database connection provider currently in use.
     * 
     * @return database connection provider currently used.
     */
    public final DbConnectionProvider getConnectionProvider() {
        // Return the database connection provider
        return this.mProvider.getOriginalProvider();
    }
    
    /**
     * Attempts to establish a connection to the database.
     * <p>By default, the generated connection has autocommit enabled.</p>
     * 
     * @return a connection to the database.
     * @throws SQLException if a database access error occurs.
     * @see it.next.sql.connection.DbConnectionProvider#getDbConnection()
     */
    private Connection getDbConnection() throws SQLException {
        // Try to get an open connection to the database
        return this.mProvider.getDbConnection(); 
    }
    
    /**
     * Indicates if the connection used by this engine is kept alive.
     * <p>Keeping a connection alive means inhibiting the closing operation,
     * which normally allows terminating communication with the database.</p>
     * 
     * @return {@code true} if the connection provided is kept alive.
     * @see #setConnectionKeepAlive(boolean)
     */
    public final boolean isConnectionKeptAlive() {
        // Invokes the internal connection provider method
        return this.mProvider.isDbConnectionKeptAlive();
    }
}
