/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.wrappers;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Defines a convenient starting point for extending a
 * {@link Connection} that wraps a pre-existing one.
 * 
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public abstract class ConnectionWrapper implements Connection
{
    /**
     * Reference to the {@link Connection} to wrap.
     */
    protected final Connection mInner;

    /**
     * Create a new {@link ConnectionWrapper}
     * wrapping a certain {@link Connection}.
     * 
     * @param inner a {@link PreparedStatement} to wrap.
     */
    public ConnectionWrapper(final Connection inner) {
        // Stores the reference to the connection
        this.mInner = Objects.requireNonNull(inner);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType,
            final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement(final int resultSetType, final int
            resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.prepareStatement(sql);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.releaseSavepoint(savepoint);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.setTypeMap(map);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int
            resultSetType, final int resultSetConcurrency) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(final String sql, final int
            resultSetType, final int resultSetConcurrency) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.prepareCall(sql, resultSetType, resultSetConcurrency);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.prepareCall(sql);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.setTransactionIsolation(level);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.setAutoCommit(autoCommit);
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
    public void setHoldability(final int holdability) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.setHoldability(holdability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.setSavepoint(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.rollback(savepoint);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.setReadOnly(readOnly);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientInfo(final String name) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getClientInfo(name);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.setCatalog(catalog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSchema(final String schema) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.setSchema(schema);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abort(final Executor executor) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.abort(executor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getTypeMap();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.nativeSQL(sql);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.isValid(timeout);
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql,
            final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.prepareStatement(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement(final int resultSetType,
            final int resultSetConcurrency) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.createStatement(resultSetType,
                resultSetConcurrency);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getMetaData();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql,
            final int autoGeneratedKeys) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.prepareStatement(sql, autoGeneratedKeys);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getTransactionIsolation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql,
            final int[] columnIndexes) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.prepareStatement(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql,
            final String[] columnNames) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.prepareStatement(sql, columnNames);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Struct createStruct(final String typeName,
            final Object[] attributes) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.createStruct(typeName, attributes);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.createStatement();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getClientInfo() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getClientInfo();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Array createArrayOf(final String typeName,
            final Object[] elements) throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.createArrayOf(typeName, elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNetworkTimeout(final Executor executor,
            final int milliseconds) throws SQLException {
        // Invokes the underlying connection method
        this.mInner.setNetworkTimeout(executor, milliseconds);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientInfo(final String name, final
            String value) throws SQLClientInfoException {
        // Invokes the underlying connection method
        this.mInner.setClientInfo(name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint setSavepoint() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.setSavepoint();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getWarnings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNetworkTimeout() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getNetworkTimeout();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getAutoCommit();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientInfo(final Properties
            properties) throws SQLClientInfoException {
        // Invokes the underlying connection method
        this.mInner.setClientInfo(properties);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SQLXML createSQLXML() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.createSQLXML();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getHoldability() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getHoldability();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.isReadOnly();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearWarnings() throws SQLException {
        // Invokes the underlying connection method
        this.mInner.clearWarnings();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCatalog() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getCatalog();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NClob createNClob() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.createNClob();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchema() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.getSchema();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.isClosed();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Blob createBlob() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.createBlob();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Clob createClob() throws SQLException {
        // Invokes the underlying connection method
        return this.mInner.createClob();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback() throws SQLException {
        // Invokes the underlying connection method
        this.mInner.rollback();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws SQLException {
        // Invokes the underlying connection method
        this.mInner.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SQLException {
        // Invokes the underlying connection method
        this.mInner.close();
    }
}
