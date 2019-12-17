/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.wrappers;

import it.stealth.sql.QueryRecord;
import it.stealth.sql.QueryResults;
import java.util.Iterator;
import java.util.Objects;

/**
 * Defines a convenient starting point for extending a
 * {@link QueryResults} that wraps a pre-existing one.
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public abstract class QueryResultsWrapper implements QueryResults
{
    /**
     * Reference to the {@link QueryResults} to wrap.
     */
    protected final QueryResults mInner;
    
    /**
     * Create a new {@link QueryResultsWrapper}
     * wrapping a certain {@link QueryResults}.
     * 
     * @param inner a {@link QueryResults} to wrap.
     */
    public QueryResultsWrapper(final QueryResults inner) {
        // Stores the reference to the QueryResults object
        this.mInner = Objects.requireNonNull(inner);
    }    

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getFieldClass(final String name) {
        // Invokes the method of the underlying object
        return this.mInner.getFieldClass(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getFieldClass(final int index) {
        // Invokes the method of the underlying object
        return this.mInner.getFieldClass(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFieldIndex(final String name) {
        // Invokes the method of the underlying object
        return this.mInner.getFieldIndex(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFieldName(final int index) {
        // Invokes the method of the underlying object
        return this.mInner.getFieldName(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSigned(final String name) {
        // Invokes the method of the underlying object
        return this.mInner.isSigned(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSigned(final int index) {
        // Invokes the method of the underlying object
        return this.mInner.isSigned(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFieldCount() {
        // Invokes the method of the underlying object
        return this.mInner.getFieldCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRecordCount() {
        // Invokes the method of the underlying object
        return this.mInner.getRecordCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasRecords() {
        // Invokes the method of the underlying object
        return this.mInner.hasRecords();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<QueryRecord> iterator() {
        // Invokes the method of the underlying object
        return this.mInner.iterator();
    }
}
