/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.readers;

import it.stealth.sql.QueryResults;
import it.stealth.sql.util.SQLQueryResults;
import it.stealth.sql.util.SQLUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Utility class that provides the main query readers to
 * read individual values from the first record of each query.
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public final class SQLQueryReaders
{
    /**
     * Defines the name of the parameter related to the class.
     */
    private static final String ClassParam = "cls";
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link Byte} value.
     */
    public static final SQLQueryReader<Byte> ByteValueReader =
            new SQLQueryReaders.STVR(Byte.class, null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link Long} value.
     */
    public static final SQLQueryReader<Long> LongValueReader =
            new SQLQueryReaders.STVR(Long.class, null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link Short} value.
     */
    public static final SQLQueryReader<Short> ShortValueReader =
            new SQLQueryReaders.STVR(Short.class, null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link Float} value.
     */
    public static final SQLQueryReader<Float> FloatValueReader =
            new SQLQueryReaders.STVR(Float.class, null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link Double} value.
     */
    public static final SQLQueryReader<Double> DoubleValueReader =
            new SQLQueryReaders.STVR(Double.class, null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link String} value.
     */
    public static final SQLQueryReader<String> StringValueReader =
            new SQLQueryReaders.STVR(String.class, null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link Object} value.
     */
    public static final SQLQueryReader<Object> ObjectValueReader =
            new SQLQueryReaders.WTVR(null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link Integer} value.
     */
    public static final SQLQueryReader<Integer> IntegerValueReader =
            new SQLQueryReaders.STVR(Integer.class, null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link Boolean} value.
     */
    public static final SQLQueryReader<Boolean> BooleanValueReader =
            new SQLQueryReaders.STVR(Boolean.class, null);
    
    /**
     * Defines the {@link SQLQueryReader} that reads the {@link ResultSet}
     * entirely, storing the results in a {@link QueryResults}.
     */
    public static final SQLQueryReader<QueryResults> DefaultReader =
            (final ResultSet rs) -> {return new SQLQueryResults(rs);};
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link LocalDate} value.
     */
    public static final SQLQueryReader<LocalDate> LocalDateValueReader =
            new SQLQueryReaders.LDQR(null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first
     * field of the first record as an {@link LocalTime} value.
     */
    public static final SQLQueryReader<LocalTime> LocalTimeValueReader =
            new SQLQueryReaders.LTQR(null);
    
    /**
     * Defines a {@link SQLQueryReader} to read the first field
     * of the first record as an {@link LocalDateTime} value.
     */
    public static final SQLQueryReader<LocalDateTime> LocalDateTimeValueReader =
            new SQLQueryReaders.LDTQR(null);
    
    /**
     * Exception message used when {@link Void}
     * is supplied as a class of field values.
     */
    private static final String ExCannotBeVoid = "The Void type"
            + " can not be used as a class of field values.";
    
    //<editor-fold defaultstate="collapsed" desc="SQLQueryReader implementation for strongly typed values.">
    /**
     * Implementation of a {@link SQLQueryReader} to read a
     * strongly typed value from the first field of the first record.
     *
     * @param <T> class of the value to be read from the query.
     */
    private static final class STVR<T> implements SQLQueryReader<T>
    {
        /**
         * Indicates the value class
         * to read from the query.
         */
        private final Class<T> mClass;
        
        /**
         * Default value returned in case
         * the query returns no result.
         */
        private final T mDefaultValue;
        
        /**
         * Constructor that specifies the class of
         * the value to read and the default value.
         *
         * @param cls class of value to be read.
         * @param defaultValue default value.
         */
        public STVR(final Class<T> cls, final T defaultValue) {
            // Stores the value returned for empty queries
            this.mDefaultValue = defaultValue;
            // Stores the class of the value to be returned
            this.mClass = cls;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final T read(final ResultSet rs) throws SQLException {
            // Check that at least one record has been returned
            if (rs.next()) {
                // Read the value of the first field of the first record
                return rs.getObject(1, this.mClass);
            } else {
                // Returns the default value
                return this.mDefaultValue;
            }
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SQLQueryReader implementation for weakly typed values.">
    /**
     * Implementation of a {@link SQLQueryReader} to read a
     * weakly typed value from the first field of the first record.
     *
     * @param <T> class of the value to be read from the query.
     */
    private static final class WTVR implements SQLQueryReader
    {   
        /**
         * Default value returned in case
         * the query returns no result.
         */
        private final Object mDefaultValue;
        
        /**
         * Class constructor that specifies the default value.
         *
         * @param defaultValue value returned for empty queries.
         */
        public WTVR(final Object defaultValue) {
            // Stores the value returned for empty queries
            this.mDefaultValue = defaultValue;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Object read(final ResultSet rs) throws SQLException {
            // Check that at least one record has been returned
            if (rs.next()) {
                // Read the value of the first
                // field of the first record
                return rs.getObject(1);
            } else {
                // Returns the default value
                return this.mDefaultValue;
            }
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SQLQueryReader implementation for LocalDateTime">
    /**
     * Implementation of a {@link SQLQueryReader} capable of reading a
     * {@link LocalDateTime} value from the first field of the first record.
     */
    private static final class LDTQR implements SQLQueryReader<LocalDateTime>
    {
        /**
         * Default value returned in case
         * the query returns no result.
         */
        private final LocalDateTime mDefaultValue;
        
        /**
         * Class constructor that specifies the default value.
         *
         * @param defaultValue value returned for empty queries.
         */
        public LDTQR(final LocalDateTime defaultValue) {
            // Stores the value returned for empty queries
            this.mDefaultValue = defaultValue;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final LocalDateTime read(final
                ResultSet rs) throws SQLException {
            // Check that at least one record has been returned
            if (rs.next()) {
                // Reads a LocalDateTime value from the first record
                return SQLUtils.readLocalDateTime(rs, 1);
            } else {
                // Returns the default value
                return this.mDefaultValue;
            }
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SQLQueryReader implementation for LocalDate">
    /**
     * Implementation of a {@link SQLQueryReader} capable of reading a
     * {@link LocalDate} value from the first field of the first record.
     */
    private static final class LDQR implements SQLQueryReader<LocalDate>
    {
        /**
         * Default value returned in case
         * the query returns no result.
         */
        private final LocalDate mDefaultValue;
        
        /**
         * Class constructor that specifies the default value.
         *
         * @param defaultValue value returned for empty queries.
         */
        public LDQR(final LocalDate defaultValue) {
            // Stores the value returned for empty queries
            this.mDefaultValue = defaultValue;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final LocalDate read(final ResultSet rs) throws SQLException {
            // Check that at least one record has been returned
            if (rs.next()) {
                // Reads a LocalDate value from the first record
                return SQLUtils.readLocalDate(rs, 1);
            } else {
                // Returns the default value
                return this.mDefaultValue;
            }
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SQLQueryReader implementation for LocalTime">
    /**
     * Implementation of a {@link SQLQueryReader} capable of reading a
     * {@link LocalTime} value from the first field of the first record.
     */
    private static final class LTQR implements SQLQueryReader<LocalTime>
    {
        /**
         * Default value returned in case
         * the query returns no result.
         */
        private final LocalTime mDefaultValue;
        
        /**
         * Class constructor that specifies the default value.
         *
         * @param defaultValue value returned for empty queries.
         */
        public LTQR(final LocalTime defaultValue) {
            // Stores the value returned for empty queries
            this.mDefaultValue = defaultValue;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final LocalTime read(final ResultSet rs) throws SQLException {
            // Check that at least one record has been returned
            if (rs.next()) {
                // Reads a LocalTime value from the first record
                return SQLUtils.readLocalTime(rs, 1);
            } else {
                // Returns the default value
                return this.mDefaultValue;
            }
        }
    }//</editor-fold>
    
    /**
     * Provides an {@link SQLQueryReader} to read
     * the first field of the first record of a query.
     * <p>If the query does not provide any records,
     * then a {@code null} value will be returned.</p>
     * 
     * @param <T> class of the value to be read by the query.
     * @param cls class of the value to be read by the query.
     * @return value read from the first field of the first record.
     * @throws NullPointerException if {@code cls} is {@code null}.
     * @throws IllegalArgumentException if {@code cls} is {@code void}.
     */
    public static final <T> SQLQueryReader<T> forClass(final Class<T> cls) {
        // Check that the class provided is valid
        SQLQueryReaders.checkFieldTypeClass(cls);
        // Check if a reader of LocalDateTime values is required
        if (LocalDateTime.class.equals(cls)) {
            // Returns the reader for LocalDateTime values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    LocalDateTimeValueReader;
        }
        // Check if a reader of LocalDate values is required
        else if (LocalDate.class.equals(cls)) {
            // Returns the reader for LocalDate values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    LocalDateValueReader;
        }
        // Check if a reader of LocalTime values is required
        else if (LocalTime.class.equals(cls)) {
            // Returns the reader for LocalTime values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    LocalTimeValueReader;
        }
        // Check if a reader of Integer values is required
        else if (Integer.class.equals(cls)) {
            // Returns the reader for Integer values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    IntegerValueReader;
        }
        // Check if a reader of Boolean values is required
        else if (Boolean.class.equals(cls)) {
            // Returns the reader for Boolean values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    BooleanValueReader;
        }
        // Check if a reader of Object values is required
        else if (Object.class.equals(cls)) {
            // Returns the reader for Object values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    ObjectValueReader;
        }
        // Check if a reader of Double values is required
        else if (Double.class.equals(cls)) {
            // Returns the reader for Double values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    DoubleValueReader;
        }
        // Check if a reader of String values is required
        else if (String.class.equals(cls)) {
            // Returns the reader for String values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    StringValueReader;
        }
        // Check if a reader of Float values is required
        else if (Float.class.equals(cls)) {
            // Returns the reader for Float values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    FloatValueReader;
        }
        // Check if a reader of Short values is required
        else if (Short.class.equals(cls)) {
            // Returns the reader for Short values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    ShortValueReader;
        }
        // Check if a reader of Byte values is required
        else if (Byte.class.equals(cls)) {
            // Returns the reader for Byte values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    ByteValueReader;
        }
        // Check if a reader of Long values is required
        else if (Long.class.equals(cls)) {
            // Returns the reader for Long values
            return (SQLQueryReader<T>) SQLQueryReaders.
                    LongValueReader;
        } else {
            // Create a reader to read strongly typed values
            return new SQLQueryReaders.STVR(cls, null);
        }
    }
    
    /**
     * Internal procedure used to check the class of the field values.
     * 
     * @param cls reference to the class of field values.
     * @throws NullPointerException if {@code cls} is {@code null}.
     * @throws IllegalArgumentException if {@code cls} is {@code void}.
     */
    private static void checkFieldTypeClass(final Object cls) {
        // Check that the reference provided is not null
        if (cls != null) {
            // Check if the supplied class identifies the Void type
            if (Void.class.equals(cls) || Void.TYPE.equals(cls)) {
                // Raises an exception indicating that the class can not be void
                throw new IllegalArgumentException(SQLQueryReaders.ExCannotBeVoid);
            }
        } else {
            // Raises an exception indicating that the class can not be null
            throw new NullPointerException(SQLQueryReaders.ClassParam);
        }
    }
    
    /**
     * Provides an {@link SQLQueryReader} to read
     * the first field of the first record of a query.
     * <p>If the query does not provide any records, then
     * the {@code defaultValue} will be returned.</p>
     * 
     * @param <T> class of the value to be read by the query.
     * @param cls class of the value to be read by the query.
     * @param defaultValue value returned for empty queries.
     * @return value read from the first field of the first record.
     * @throws NullPointerException if {@code cls} is {@code null}.
     * @throws IllegalArgumentException if {@code cls} is {@code void}.
     */
    public static final <T> SQLQueryReader<T> forClass(
            final Class<T> cls, final T defaultValue) {
        // Check if the default value is different from null
        if (defaultValue != null) {
            // Check that the class provided is valid
            SQLQueryReaders.checkFieldTypeClass(cls);
            // Check if a reader of LocalDateTime values is required
            if (LocalDateTime.class.equals(cls)) {
                // Create a reader to read LocalDateTime values
                return (SQLQueryReader<T>) new SQLQueryReaders.LDTQR(
                        LocalDateTime.class.cast(defaultValue));
            }
            // Check if a reader of LocalDate values is required
            else if (LocalDate.class.equals(cls)) {
                // Create a reader to read LocalDate values
                return (SQLQueryReader<T>) new SQLQueryReaders.LDQR(
                        LocalDate.class.cast(defaultValue));
            }
            // Check if a reader of LocalTime values is required
            else if (LocalTime.class.equals(cls)) {
                // Create a reader to read LocalTime values
                return (SQLQueryReader<T>) new SQLQueryReaders.LTQR(
                        LocalTime.class.cast(defaultValue));
            }
            // Check if a reader of Object values is required
            else if (Object.class.equals(cls)) {
                // Create a reader to read weakly typed values
                return new SQLQueryReaders.WTVR(defaultValue);
            } else {
                // Create a reader to read strongly typed values
                return new SQLQueryReaders.STVR(
                        cls, defaultValue);
            }
        } else {
            // Invokes the method used for
            // readers with a null default value
            return SQLQueryReaders.forClass(cls);
        }
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private SQLQueryReaders() {}
}
