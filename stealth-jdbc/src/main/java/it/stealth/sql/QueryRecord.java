/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Interface that defines a record returned by a query.
 * <p>These records are grouped and provided through a {@link QueryResults}
 * object.</p><p>Each record provides, in addition to the values of its fields,
 * also the metadata associated with it.</p><p>This interface exposes methods to
 * get the values of the record in a strongly typed way. For each type of data
 * it's always possible to request the value of a field by name or by index. In
 * general, using the field index will be more efficient.</p><p>Furthermore, each
 * {@code "get"}  method also handles conversions between data types where possible.
 * Otherwise, an exception of type {@link ClassCastException} is raised.</p><p>For
 * example an integer field should be obtained through the {@code getInteger()}
 * method but the interface should also handle the conversion when it's requested
 * through {@code getString()}. Otherwise conversions between incompatible types
 * are not managed (for example between dates and boolean values).</p><p>For more
 * information on allowed conversions see the JDBC specifications.</p>
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 * @since 2.0.8
 */
public interface QueryRecord
{
    /**
     * Gets the number of fields that make up this record.
     * 
     * @return number of fields in this record.
     * @see QueryResults#getFieldCount()
     */
    public int getFieldCount();
    
    /**
     * Indicates if a certain field has signed numbers.
     * 
     * @param fieldName name of the requested field.
     * @return {@code true} if the field has signed
     *          numbers, {@code false} otherwise.
     * @see QueryResults#isSigned(java.lang.String)
     */
    public boolean isSigned(final String fieldName);
    
    /**
     * Indicates if a certain field has signed numbers.
     * 
     * @param fieldIndex index of the requested field.
     * @return {@code true} if the field has signed
     *          numbers, {@code false} otherwise.
     * @see QueryResults#isSigned(int)
     */
    public boolean isSigned(final int fieldIndex);
    
    /**
     * Gets the position index of a certain field.
     * 
     * @param fieldName name of the requested field.
     * @return position index of the requested field.
     * @see QueryResults#getFieldIndex(java.lang.String)
     */
    public int getFieldIndex(final String fieldName);
    
    /**
     * Gets the name of a certain field.
     * 
     * @param fieldIndex index of the requested field.
     * @return name of the requested field.
     * @see QueryResults#getFieldName(int)
     */
    public String getFieldName(final int fieldIndex);
    
    /**
     * Gets the data class of a certain field.
     * 
     * @param fieldIndex index of the requested field.
     * @return class of the data of this field.
     * @see QueryResults#getFieldClass(int)
     */
    public Class<?> getFieldClass(final int fieldIndex);
    
    /**
     * Gets the data class of a certain field.
     * 
     * @param fieldName name of the requested field.
     * @return class of the data of this field.
     * @see QueryResults#getFieldClass(java.lang.String)
     */
    public Class<?> getFieldClass(final String fieldName);
    
    /**
     * Gets the value of the specified field in a strongly typed way.
     * 
     * @param <T> class of the value provided by the specified field.
     * @param cls class of the value provided by the specified field.
     * @param fieldName name of the field from which to get the value.
     * @return strongly typed value of the requested field.
     * @throws ClassCastException if the value cannot be converted.
     */
    public <T> T getObject(final Class<T> cls, final String fieldName);
    
    /**
     * Gets the value of the specified field in a strongly typed way.
     * 
     * @param <T> class of the value provided by the specified field.
     * @param cls class of the value provided by the specified field.
     * @param fieldIndex index of the field from which to get the value.
     * @return strongly typed value of the requested field.
     * @throws ClassCastException if the value cannot be converted.
     */
    public <T> T getObject(final Class<T> cls, final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link LocalDateTime}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link LocalDateTime}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public LocalDateTime getTimestamp(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link LocalDateTime}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link LocalDateTime}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public LocalDateTime getTimestamp(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link BigDecimal}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link BigDecimal}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public BigDecimal getBigDecimal(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link BigDecimal}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link BigDecimal}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public BigDecimal getBigDecimal(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link BigInteger}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link BigInteger}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public BigInteger getBigInteger(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link BigInteger}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link BigInteger}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public BigInteger getBigInteger(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link Character}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link Character}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Character getCharacter(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link Character}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link Character}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Character getCharacter(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link Integer}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link Integer}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Integer getInteger(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link Integer}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link Integer}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Integer getInteger(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link Boolean}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link Boolean}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Boolean getBoolean(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link Boolean}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link Boolean}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Boolean getBoolean(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link LocalDate}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link LocalDate}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public LocalDate getDate(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link LocalDate}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link LocalDate}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public LocalDate getDate(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link LocalTime}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link LocalTime}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public LocalTime getTime(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link LocalTime}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link LocalTime}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public LocalTime getTime(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link java.lang.String}.
     * <p>This method never raises a {@link ClassCastException} because the
     * {@link Object#toString()} method can be invoked on each value.</p>
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.String}.
     */
    public String getString(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link java.lang.String}.
     * <p>This method never raises a {@link ClassCastException} because the
     * {@link Object#toString()} method can be invoked on each value.</p>
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.String}.
     */
    public String getString(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as a byte array.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as a byte array.
     * @throws ClassCastException if the value cannot be converted.
     */
    public byte[] getBytes(final String fieldName);
    
    /**
     * Gets the value of the field designated as a byte array.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as a byte array.
     * @throws ClassCastException if the value cannot be converted.
     */
    public byte[] getBytes(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Double}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Double}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Double getDouble(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Double}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Double}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Double getDouble(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as a generic object.
     * <p>It represents the weakly typed version of the {@link
     * #getObject(java.lang.Class, java.lang.String)} method.</p>
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as a generic object.
     */
    public Object getObject(final String fieldName);
    
    /**
     * Gets the value of the field designated as a generic object.
     * <p>It represents the weakly typed version of the {@link
     * #getObject(java.lang.Class, int)} method.</p>
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as a generic object.
     */
    public Object getObject(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Float}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Float}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Float getFloat(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Float}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Float}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Float getFloat(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Short}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Short}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Short getShort(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Short}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Short}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Short getShort(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Long}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Long}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Long getLong(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Long}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Long}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Long getLong(final int fieldIndex);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Byte}.
     * 
     * @param fieldName name of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Byte}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Byte getByte(final String fieldName);
    
    /**
     * Gets the value of the field designated as {@link java.lang.Byte}.
     * 
     * @param fieldIndex index of the field from which to get the value.
     * @return value of the field designated as {@link java.lang.Byte}.
     * @throws ClassCastException if the value cannot be converted.
     */
    public Byte getByte(final int fieldIndex);
}
