/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.connection;

import java.sql.Connection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import it.stealth.sql.wrappers.ConnectionWrapper;

/**
 * Wrapper for {@link Connection} instances that inhibit the closing operation.
 * <p>In addition to inhibiting the closure, this implementation takes care to
 * report each value set by the various {@code set()} methods to its original
 * value (ie the one it had when creating this wrapper).</p><p>This operation
 * is performed just by calling the {@link UnclosableConnection#close()}
 * method.</p>
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public class UnclosableConnection extends ConnectionWrapper
{
    private int mFlag;
    private String mSchema;
    private String mCatalog;
    private int mHoldability;
    private boolean mReadOnly;
    private boolean mAutoCommit;
    private Properties mProperties;
    private int mTransactionIsolation;
    private Map<String, Class<?>> mTypeMap;
    
    private static final String UnableCloneProps =
            "Unable to clone client information properties.";
    
    private static final int TransactionIsolationProp = 1;
    
    private static final int HoldabilityProp = 2;
    
    private static final int AutoCommitProp = 4;
    
    private static final int ClientInfoProp = 8;
    
    private static final int ReadOnlyProp = 16;
    
    private static final int CatalogProp = 32;
    
    private static final int TypeMapProp = 64;
    
    private static final int SchemaProp = 128;
    
    public UnclosableConnection(final Connection inner) {
        super(UnclosableConnection.unwrap(inner));
        this.mFlag = 0;
    }

    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        this.mTransactionIsolation = this.mInner.getTransactionIsolation();
        this.mFlag |= UnclosableConnection.TransactionIsolationProp;
        super.setTransactionIsolation(level);
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.mAutoCommit = this.mInner.getAutoCommit();
        this.mFlag |= UnclosableConnection.AutoCommitProp;
        super.setAutoCommit(autoCommit);
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        this.mHoldability = this.mInner.getHoldability();
        this.mFlag |= UnclosableConnection.HoldabilityProp;
        super.setHoldability(holdability);
    }

    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        this.mReadOnly = this.mInner.isReadOnly();
        this.mFlag |= UnclosableConnection.ReadOnlyProp;
        super.setReadOnly(readOnly);
    }

    @Override
    public void setCatalog(final String catalog) throws SQLException {
        this.mCatalog = this.mInner.getCatalog();
        this.mFlag |= UnclosableConnection.CatalogProp;
        super.setCatalog(catalog);
    }

    @Override
    public void setSchema(final String schema) throws SQLException {
        this.mSchema = this.mInner.getSchema();
        this.mFlag |= UnclosableConnection.SchemaProp;
        super.setSchema(schema);
    }

    @Override
    public void setClientInfo(final String name, final
            String value) throws SQLClientInfoException {
        this.cloneClientInfoProperties();
        this.mFlag |= UnclosableConnection.ClientInfoProp;
        super.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(final Properties
            properties) throws SQLClientInfoException {
        this.cloneClientInfoProperties();
        this.mFlag |= UnclosableConnection.ClientInfoProp;
        super.setClientInfo(properties);
    }
    
    @Override
    public void setTypeMap(final Map<String,
            Class<?>> map) throws SQLException {
        this.mTypeMap = this.mInner.getTypeMap();
        this.mFlag |= UnclosableConnection.TypeMapProp;
        super.setTypeMap(map);
    }
    
    /**
     * Internal method that is called to close the internal connection.
     *
     * @throws SQLException if a database access error occurs.
     */
    public void closeInner() throws SQLException {
        // Closes the internal connection
        this.mInner.close();
    }

    @Override
    public void close() throws SQLException {
        if ((this.mFlag & UnclosableConnection.TransactionIsolationProp) != 0) {
            this.mInner.setTransactionIsolation(this.mTransactionIsolation);
        }
        if ((this.mFlag & UnclosableConnection.HoldabilityProp) != 0) {
            this.mInner.setHoldability(this.mHoldability);
        }
        if ((this.mFlag & UnclosableConnection.AutoCommitProp) != 0) {
            this.mInner.setAutoCommit(this.mAutoCommit);
        }
        if ((this.mFlag & UnclosableConnection.ClientInfoProp) != 0) {
            this.mInner.setClientInfo(this.mProperties);
        }
        if ((this.mFlag & UnclosableConnection.ReadOnlyProp) != 0) {
            this.mInner.setReadOnly(this.mReadOnly);
        }
        if ((this.mFlag & UnclosableConnection.CatalogProp) != 0) {
            this.mInner.setCatalog(this.mCatalog);
        }
        if ((this.mFlag & UnclosableConnection.TypeMapProp) != 0) {
            this.mInner.setTypeMap(this.mTypeMap);
        }
        if ((this.mFlag & UnclosableConnection.SchemaProp) != 0) {
            this.mInner.setSchema(this.mSchema);
        }
        this.mFlag = 0;
    }
    
    private static Properties cloneProps(final Connection
            inner) throws SQLClientInfoException {
        final Properties props;
        try {
            props = inner.getClientInfo();
            return (Properties) props.clone();
        }
        catch (final SQLException ex) {
            throw new SQLClientInfoException(UnclosableConnection.
                    UnableCloneProps, null, ex);
        }
    }
    
    private static Connection unwrap(final Connection conn) {
        if (conn instanceof UnclosableConnection) {
            return ((UnclosableConnection)conn).mInner;
        } else {
            return conn;
        }
    }
    
    private synchronized void cloneClientInfoProperties()
            throws SQLClientInfoException {
        if (this.mProperties == null) {
            this.mProperties = UnclosableConnection.
                    cloneProps(this.mInner);
        }
    }
}