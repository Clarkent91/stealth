/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.readers;

import it.stealth.sql.SQLEngine;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Defines the interface for a reader of results of a database query.
 * <p>To read the results of a query, you need to implement this interface and
 * then provide that object as argument to the {@link SQLEngine#query(String,
 * SQLQueryReader)} method together with the SQL query to be submitted to
 * the database.</p>
 * 
 * @param <T> type of the object returned by the query.
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
@FunctionalInterface
public interface SQLQueryReader<T>
{
    /**
     * Reads the results produced by a query to the database.
     * 
     * @param rs a {@link ResultSet} containing the query results.
     * @return object that encapsulates the query results.
     * @throws SQLException if an error occurs while querying the database.
     */
    public T read(final ResultSet rs) throws SQLException;
}
