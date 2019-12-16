/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql;

import java.sql.ResultSet;

/**
 * Interface that encapsulates a generic set of results produced by a query to a
 * database.<p>These results are provided in tabular form as a {@link QueryRecord}
 * sequence. It can be considered the <i>&quot;disconnected&quot;</i> version of
 * a {@link ResultSet}. Unlike the {@link ResultSet}, in fact, this object has no
 * connection, since all the information resides in local memory.</p><p>Moreover,
 * this object appears to be immutable and read-only, so its instance can be
 * shared between multiple threads simultaneously without any side effects.</p>
 * <h3>Usage:</h3>Below we show an example of code that uses {@link QueryResults}.
 * Suppose we query a table of people, and want to request the name and the name
 * of people over 18 years old.<p><code>String sqlQuery = &quot;SELECT name,
 * surname FROM person WHERE age &gt; 18&quot;;<br><br>QueryResults results =
 * sqlEngine.query(sqlQuery);<br><br>for (QueryRecord record : results) {<br>
 * &nbsp; &nbsp; System.out.println(record.getString(&quot;name&quot;));<br>
 * &nbsp; &nbsp; System.out.println(record.getString(&quot;surname&quot;));<br>
 * }</code></p>
 *
 * @author Fabrizio Lo Verde
 * @see QueryRecord
 * @version 1.0
 * @since 2.0.8
 */
public interface QueryResults extends Iterable<QueryRecord>
{
    /**
     * Gets the data class of a certain field.
     * 
     * @param name name of the requested field.
     * @return class of the data of this field.
     * @see QueryRecord#getFieldClass(java.lang.String)
     */
    public Class<?> getFieldClass(final String name);
    
    /**
     * Gets the data class of a certain field.
     * 
     * @param index index of the requested field.
     * @return class of the data of this field.
     * @see QueryRecord#getFieldClass(int)
     */
    public Class<?> getFieldClass(final int index);
    
    /**
     * Gets the position index of a certain field.
     * 
     * @param name name of the requested field.
     * @return position index of the requested field.
     * @see QueryRecord#getFieldIndex(java.lang.String)
     */
    public int getFieldIndex(final String name);
    
    /**
     * Gets the name of a certain field.
     * 
     * @param index index of the requested field.
     * @return name of the requested field.
     * @see QueryRecord#getFieldName(int)
     */
    public String getFieldName(final int index);
    
    /**
     * Indicates if a certain field has signed numbers.
     * 
     * @param name name of the requested field.
     * @return {@code true} if the field has signed
     *          numbers, {@code false} otherwise.
     * @see QueryRecord#isSigned(java.lang.String)
     */
    public boolean isSigned(final String name);
    
    /**
     * Indicates if a certain field has signed numbers.
     * 
     * @param index index of the requested field.
     * @return {@code true} if the field has signed
     *          numbers, {@code false} otherwise.
     * @see QueryRecord#isSigned(int)
     */
    public boolean isSigned(final int index);
    
    /**
     * Gets the total number of fields returned the query.
     * 
     * @return number of fields returned by the query.
     * @see QueryRecord#getFieldCount()
     */
    public int getFieldCount();
    
    /**
     * Gets the number of total records returned by the query.
     * 
     * @return number of records returned by the query.
     */
    public int getRecordCount();
    
    /**
     * Indicates the query produced at least one result.
     * 
     * @return {@code true} if at least one record is
     *         contained, {@code false} otherwise.
     */
    public boolean hasRecords();
}
