/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.wrappers;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Objects;

/**
 * Defines a convenient starting point for extending a
 * {@link PreparedStatement} that wraps a pre-existing one.
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public abstract class PreparedStatementWrapper implements PreparedStatement
{
    /**
     * Reference to the {@link PreparedStatement} to wrap.
     */
    protected final PreparedStatement mInner;
    
    /**
     * Create a new {@link PreparedStatementWrapper}
     * wrapping a certain {@link PreparedStatement}.
     * 
     * @param inner a {@link PreparedStatement} to wrap.
     */
    public PreparedStatementWrapper(final PreparedStatement inner) {
        // Stores the reference to the statement
        this.mInner = Objects.requireNonNull(inner);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet executeQuery() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeQuery();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setNull(parameterIndex, sqlType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setBoolean(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setByte(final int parameterIndex, final byte x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setByte(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShort(final int parameterIndex, final short x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setShort(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInt(final int parameterIndex, final int x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setInt(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLong(final int parameterIndex, final long x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setLong(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFloat(final int parameterIndex, final float x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setFloat(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDouble(final int parameterIndex, final double x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setDouble(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setBigDecimal(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setString(final int parameterIndex, final String x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setString(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setBytes(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDate(final int parameterIndex, final Date x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setDate(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTime(final int parameterIndex, final Time x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setTime(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setTimestamp(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setAsciiStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setUnicodeStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setBinaryStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearParameters() throws SQLException {
        // Invokes the underlying statement method
        this.mInner.clearParameters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setObject(parameterIndex, x, targetSqlType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObject(final int parameterIndex, final Object x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setObject(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBatch() throws SQLException {
        // Invokes the underlying statement method
        this.mInner.addBatch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setCharacterStream(parameterIndex, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRef(final int parameterIndex, final Ref x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setRef(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlob(final int parameterIndex, final Blob x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setBlob(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClob(final int parameterIndex, final Clob x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setClob(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArray(final int parameterIndex, final Array x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setArray(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getMetaData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setDate(parameterIndex, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setTime(parameterIndex, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setTimestamp(parameterIndex, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNull(final int parameterIndex, final int sqlType, final String typeName) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setNull(parameterIndex, sqlType, typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setURL(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getParameterMetaData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setRowId(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNString(final int parameterIndex, final String value) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setNString(parameterIndex, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setNCharacterStream(parameterIndex, value, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setNClob(parameterIndex, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setNClob(parameterIndex, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setBlob(parameterIndex, inputStream, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setNClob(parameterIndex, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setSQLXML(parameterIndex, xmlObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scaleOrLength) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setAsciiStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setBinaryStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setCharacterStream(parameterIndex, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setAsciiStream(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setBinaryStream(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setCharacterStream(parameterIndex, reader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setNCharacterStream(parameterIndex, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setClob(parameterIndex, reader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setBlob(parameterIndex, inputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setNClob(parameterIndex, reader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObject(final int parameterIndex, final Object x, final SQLType targetSqlType, final int scaleOrLength) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObject(final int parameterIndex, final Object x, final SQLType targetSqlType) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setObject(parameterIndex, x, targetSqlType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long executeLargeUpdate() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeLargeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate(final String sql) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeUpdate(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SQLException {
        // Invokes the underlying statement method
        this.mInner.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxFieldSize() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getMaxFieldSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxFieldSize(final int max) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setMaxFieldSize(max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxRows() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getMaxRows();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxRows(final int max) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setMaxRows(max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEscapeProcessing(final boolean enable) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setEscapeProcessing(enable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getQueryTimeout() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getQueryTimeout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQueryTimeout(final int seconds) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setQueryTimeout(seconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel() throws SQLException {
        // Invokes the underlying statement method
        this.mInner.cancel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getWarnings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearWarnings() throws SQLException {
        // Invokes the underlying statement method
        this.mInner.clearWarnings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCursorName(final String name) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.clearWarnings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(final String sql) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.execute(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getResultSet() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getResultSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUpdateCount() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getUpdateCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getMoreResults() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getMoreResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setFetchDirection(direction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFetchDirection() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getFetchDirection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setFetchSize(rows);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFetchSize() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getFetchSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getResultSetConcurrency() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getResultSetConcurrency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getResultSetType() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getResultSetType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBatch(final String sql) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.addBatch(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearBatch() throws SQLException {
        // Invokes the underlying statement method
        this.mInner.clearBatch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] executeBatch() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeBatch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getMoreResults(final int current) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getMoreResults(current);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getGeneratedKeys();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeUpdate(sql, autoGeneratedKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeUpdate(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate(final String sql, final String[] columnNames) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeUpdate(sql, columnNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.execute(sql, autoGeneratedKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.execute(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(final String sql, final String[] columnNames) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.execute(sql, columnNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getResultSetHoldability() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.getResultSetHoldability();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.isClosed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setPoolable(poolable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPoolable() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.isPoolable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeOnCompletion() throws SQLException {
        // Invokes the underlying statement method
        this.mInner.closeOnCompletion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.isCloseOnCompletion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLargeMaxRows(final long max) throws SQLException {
        // Invokes the underlying statement method
        this.mInner.setLargeMaxRows(max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long[] executeLargeBatch() throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeLargeBatch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long executeLargeUpdate(final String sql) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeLargeUpdate(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long executeLargeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeLargeUpdate(sql, autoGeneratedKeys);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long executeLargeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeLargeUpdate(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long executeLargeUpdate(final String sql, final String[] columnNames) throws SQLException {
        // Invokes the underlying statement method
        return this.mInner.executeLargeUpdate(sql, columnNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        // Check if this class extends or implements the supplied
        // one, otherwise invokes the underlying connection method
        return iface.isAssignableFrom(this.getClass()) ||
                this.mInner.isWrapperFor(iface);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        // Check if this class extends or implements the supplied one
        if (iface.isAssignableFrom(this.getClass())) {
            // Cast this instance to the specified class
            return iface.cast(this);
        } else {
            // Invokes the underlying connection method
            return this.mInner.unwrap(iface);
        }
    }
}