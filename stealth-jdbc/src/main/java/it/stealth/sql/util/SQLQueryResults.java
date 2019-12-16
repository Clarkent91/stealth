/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.util;

import it.stealth.sql.QueryRecord;
import it.stealth.sql.QueryResults;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import java.math.BigInteger;
import java.sql.SQLDataException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

/**
 * Standard implementation of the {@link QueryResults} interface that
 * reads and stores the results provided through a {@link ResultSet} object.
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 * @since 2.0.8
 */
public class SQLQueryResults implements QueryResults
{
    /**
     * Number of columns that make up the results.
     */
    private final int mColumnCount;
    
    /**
     * Collection of records produced by the query.
     */
    private final Collection<QueryRecord> mRows;
    
    /**
     * Arrays of the fields that make up
     * the results (in the original order).
     */
    private final SQLQueryResults.Field[] mFields;
    
    /**
     * Arrays of the fields that make
     * up the results (sorted by name).
     */
    private final SQLQueryResults.Field[] mSortedFields;
    
    /**
     * Exception message raised in case of out-of-range values.
     */
    private static final String ExUnsignedOverflow = "'%s' in column '%d'"
            + " is outside valid range for the datatype UNSIGNED %s";
    
    /**
     * Defines the SQLState code for out-of-range values.
     */
    private static final String SQLStateOutOfRange = "22003";
    
    /**
     * Suffix used to compose the message of
     * the exception related to an overflow error.
     */
    private static final String OverflowSuffix = " overflow";
    
    /**
     * Textual representation of an empty {@link QueryResults}.
     */
    private static final String NoResultsLabel = "(empty)";
    
    /**
     * Exception message raised in case of
     * conversion between incompatible types.
     */
    private static final String ExCannotConvert =
            "Unable to convert an %s value to a %s value.";
    
    /**
     * Exception message raised when reading a binary field.
     */
    private static final String ExReadingBinaryField =
            "Error when reading the binary field \"%s\".";
    
    /**
     * Defines the initial capacity of the
     * copy buffer used for binary fields.
     */
    private static final int BufferSize = 1024;
    
    /**
     * Create a {@link SQLQueryResults} by reading and storing
     * the results provided by a {@link ResultSet} object.
     * 
     * @param rs {@link ResultSet} object from which to read data.
     * @throws SQLException if errors occur while reading the data.
     */
    public SQLQueryResults(final ResultSet rs) throws SQLException {
        // Invoke the overloaded constructor
        this(rs, ZoneId.systemDefault());
    }
    
    /**
     * Create a {@link SQLQueryResults} by reading and storing
     * the results provided by a {@link ResultSet} object.
     * <p>It's also specify a {@link ZoneId} object to
     * indicate the time zone for localized dates.</p>
     * 
     * @param rs {@link ResultSet} object from which to read data.
     * @param zoneId indication of the time-zone for the localized dates.
     * @throws SQLException if errors occur while reading the data.
     */
    public SQLQueryResults(final ResultSet rs, final
            ZoneId zoneId) throws SQLException {
        // Declares a support integer
        int i;
        // Declares a support reference
        Mutable<byte[]> bytes;
        // Declares a support reference
        SQLQueryResults.Row row;
        // Declares a support reference
        SQLQueryResults.Field field;
        // Declares a support reference
        final ResultSetMetaData metaData;
        // Declares a support reference
        final Collection<QueryRecord> rows;
        // Get the metadata from the ResultSet
        metaData = rs.getMetaData();
        // Instantiates a list to collect the records provided
        rows = new LinkedList<>();
        // Gets the number of columns that make up the results provided
        this.mColumnCount = metaData.getColumnCount();
        // Wraps the collection of records to make it unmodifiable
        this.mRows = Collections.unmodifiableCollection(rows);
        // Allocate an array to keep information about the fields
        this.mFields = new SQLQueryResults.Field[this.mColumnCount];
        // Allocate another array by sorting the data structures by name
        this.mSortedFields = new SQLQueryResults.Field[this.mColumnCount];
        // Iterates over all the columns returned by the ResultSet
        for (i = 0, bytes = null; i < this.mColumnCount; i++) {
            // Create a data structure for each result column
            field = new SQLQueryResults.Field(metaData, i);
            // Check if a buffer needs to be allocated for binary fields
            if (field.isBinaryField() && bytes == null) {
                // Initializes an indirect reference to the copy buffer
                bytes = new MutableObject<>();
            }
            // Stores the reference to this structure
            this.mSortedFields[i] = field;
            // Stores the reference to this structure
            this.mFields[i] = field;
        }
        // Sort the array of fields based on their name
        Arrays.sort(this.mSortedFields);
        // Itera over all records provided by the ResultSet
        while (rs.next()) {
            // Instantiates a new object to store
            // the values of the current record
            row = new SQLQueryResults.Row(this);
            // Itera over all fields of the record
            for (i = 0; i < this.mColumnCount; i++) {
                // Gets the i-th field of the record
                field = this.mFields[i];
                // Check the data type of the field
                switch (field.mSQLType) {
                    // Identifies generic SQL type for byte values
                    case Types.TINYINT:
                        // Check if the field has the sign
                        if (field.mSigned) {
                            // Read and stores the value of the field
                            row.mData[i] = SQLUtils.readByte(rs, i + 1);
                        } else {
                            // Read and stores the value of the field
                            row.mData[i] = SQLQueryResults.readUnsignedByte(rs, i + 1);
                        }
                    break;
                    // Identifies generic SQL type for short values
                    case Types.SMALLINT:
                        // Check if the field has the sign
                        if (field.mSigned) {
                            // Read and stores the value of the field
                            row.mData[i] = SQLUtils.readShort(rs, i + 1);
                        } else {
                            // Read and stores the value of the field
                            row.mData[i] = SQLQueryResults.readUnsignedShort(rs, i + 1);
                        }
                    break;
                    // Identifies the generic SQL type DATE
                    case Types.DATE:
                        // Read and stores the value of the field
                        row.mData[i] = SQLUtils.readLocalDate(rs, i + 1);
                    break;
                    // Identifies the generic SQL type TIME
                    case Types.TIME:
                        // Read and stores the value of the field
                        row.mData[i] = SQLUtils.readLocalTime(rs, i + 1);
                    break;
                    // Identifies the generic SQL type TIMESTAMP
                    case Types.TIMESTAMP:
                        // Read and stores the value of the field
                        row.mData[i] = SQLUtils.readLocalDateTime(rs, i + 1);
                    break;
                    // Identifies the generic SQL type TIME with timezone
                    case Types.TIME_WITH_TIMEZONE:
                        // Read and stores the value of the field
                        row.mData[i] = SQLUtils.readOffsetTime(rs, i + 1, zoneId);
                    break;
                    // Identifies the generic SQL type TIMESTAMP with timezone
                    case Types.TIMESTAMP_WITH_TIMEZONE:
                        // Read and stores the value of the field
                        row.mData[i] = SQLUtils.readOffsetDateTime(rs, i + 1, zoneId);
                    break;
                    // Identifies generic SQL types for binary fields
                    case Types.VARBINARY: case Types.BINARY:
                    case Types.BLOB: case Types.LONGVARBINARY:
                        // Read and stores the contents of the binary field
                        row.mData[i] = SQLQueryResults.readBytes(rs, field, bytes);
                    break;
                    // For all other SQL types...
                    default:
                        // Read and stores the value of the field
                        row.mData[i] = rs.getObject(i + 1);
                    break;
                }
            }
            // Add the record to the list
            rows.add(row);
        }
    }
    
    /**
     * Internal data structure that maintains
     * information related to a certain column.
     */
    private static final class Field implements
            Comparable<SQLQueryResults.Field>
    {        
        /**
         * Field position index.
         */
        private final int mIndex;
        
        /**
         * Class of data provided by this field.
         */
        private final Class mCls;
        
        /**
         * Name used to refer to this field.
         */
        private final String mName;
        
        /**
         * SQL code for the field type.
         */
        private final int mSQLType;
        
        /**
         * Indicates if the field held signed numbers.
         */
        private final boolean mSigned;
        
        /**
         * Acquires information on the i-th field.
         * 
         * @param md metadata of query results.
         * @param i index of the field involved.
         * @throws SQLException if errors occur
         *         during information retrieval.
         */
        public Field(final ResultSetMetaData md,
                final int i) throws SQLException {
            // Stores the flag of the sign
            this.mSigned = md.isSigned(i + 1);
            // Stores the name of this field
            this.mName = md.getColumnLabel(i + 1);
            // Stores the SQL type of this field
            this.mSQLType = md.getColumnType(i + 1);
            // Get the respective Java class for this field
            this.mCls = SQLQueryResults.mapSQLType(
                    this.mSQLType, this.mSigned);
            // Stores the position index of the field
            this.mIndex = i;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final int compareTo(final SQLQueryResults.Field other) {
            // Compare between their field names in case-insensitive
            return this.mName.compareToIgnoreCase(other.mName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final boolean equals(final Object obj) {
            // Check if it is the same object,
            // or if they have the same field name
            return obj == this || obj != null && this.mName.
                    equalsIgnoreCase(obj.toString());
        }
        
        /**
         * Internal procedure that indicates
         * whether this field is a binary field.
         * 
         * @return {@code true} if it is a binary field.
         */
        private boolean isBinaryField() {
            // Check the data type of this field
            switch (this.mSQLType) {
                // Identifies generic SQL types for binary fields
                case Types.VARBINARY: case Types.BINARY:
                case Types.BLOB: case Types.LONGVARBINARY:
                    // Return true
                    return true;
                // For all other SQL types...
                default:
                    // Return false
                    return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final int hashCode() {
            // Calculate the hash code for this object
            return 163 + this.mName.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final String toString() {
            // Use the name as text representation
            return this.mName;
        }
    }
    
    /**
     * Internal data structure that maintains the
     * values of a certain record provided by the query.
     */
    private static final class Row implements QueryRecord
    {
        /**
         * Array of record values.
         */
        private final Object[] mData;
        
        /**
         * Set of results to which it belongs.
         */
        private final SQLQueryResults mOwner;
        
        /**
         * Create a new object to store the values of a record.
         * 
         * @param owner set of results to which it belongs.
         */
        private Row(final SQLQueryResults owner) {
            // Allocate the array of values for this record
            this.mData = new Object[owner.mColumnCount];
            // Stores the reference to the owner
            this.mOwner = owner;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Object getObject(final int columnIndex) {
            // Returns the value of the requested field
            return this.mData[columnIndex];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final LocalDateTime getTimestamp(final int columnIndex) {
            // Check the data type of the field specified
            switch (this.mOwner.mFields[columnIndex].mSQLType) {
                // Identifies the generic SQL type TIMESTAMP
                case Types.TIMESTAMP:
                    // Cast the field value to LocalDateTime
                    return (LocalDateTime) this.mData[columnIndex];
                // Identifies the generic SQL type TIMESTAMP with timezone
                case Types.TIMESTAMP_WITH_TIMEZONE:
                    // Converts the field value to a LocalDateTime
                    return SQLQueryResults.toLocalDateTime((OffsetDateTime)
                            this.mData[columnIndex]);
                // Identifies the generic SQL type DATE
                case Types.DATE:
                    // Returns the midnight of the date of this field
                    return SQLQueryResults.atMidnight((LocalDate)
                            this.mData[columnIndex]);
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            LocalDateTime.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final BigDecimal getBigDecimal(final int columnIndex) {
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Check the data type of the field specified
            switch (field.mSQLType) {
                // Identifies generic SQL types for big-decimal values
                case Types.NUMERIC: case Types.DECIMAL:
                    // Cast the field value to BigDecimal
                    return (BigDecimal) this.mData[columnIndex];
                // Identifies generic SQL types for boolean values
                case Types.BIT: case Types.BOOLEAN:
                    // Converts the field value to a BigDecimal
                    return SQLQueryResults.toBigDecimal((Boolean)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for byte values
                case Types.TINYINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a BigDecimal
                        return SQLQueryResults.toBigDecimal((Byte)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a BigDecimal
                        return SQLQueryResults.toBigDecimal((Short)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for short values
                case Types.SMALLINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a BigDecimal
                        return SQLQueryResults.toBigDecimal((Short)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a BigDecimal
                        return SQLQueryResults.toBigDecimal((Integer)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for integer values
                case Types.INTEGER:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a BigDecimal
                        return SQLQueryResults.toBigDecimal((Integer)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a BigDecimal
                        return SQLQueryResults.toBigDecimal((Long)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for long values
                case Types.BIGINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a BigDecimal
                        return SQLQueryResults.toBigDecimal((Long)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a BigDecimal
                        return SQLQueryResults.toBigDecimal((BigInteger)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for float values
                case Types.REAL:
                    // Converts the field value to a BigDecimal
                    return SQLQueryResults.toBigDecimal((Float)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for double values
                case Types.FLOAT: case Types.DOUBLE:
                    // Converts the field value to a BigDecimal
                    return SQLQueryResults.toBigDecimal((Double)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for string values
                case Types.VARCHAR: case Types.LONGVARCHAR:
                case Types.NVARCHAR: case Types.LONGNVARCHAR:
                    // Converts the field value to a BigDecimal
                    return NumberUtils.createBigDecimal(this.
                            getString(columnIndex));
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            BigDecimal.class));
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final BigInteger getBigInteger(final int columnIndex) {
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Check the data type of the field specified
            switch (field.mSQLType) {
                // Identifies generic SQL types for big-decimal values
                case Types.NUMERIC: case Types.DECIMAL:
                    // Cast the field value to BigInteger
                    return SQLQueryResults.toBigInteger((BigDecimal)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for boolean values
                case Types.BIT: case Types.BOOLEAN:
                    // Converts the field value to a BigInteger
                    return SQLQueryResults.toBigInteger((Boolean)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for byte values
                case Types.TINYINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a BigInteger
                        return SQLQueryResults.toBigInteger((Byte)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a BigInteger
                        return SQLQueryResults.toBigInteger((Short)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for short values
                case Types.SMALLINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a BigInteger
                        return SQLQueryResults.toBigInteger((Short)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a BigInteger
                        return SQLQueryResults.toBigInteger((Integer)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for integer values
                case Types.INTEGER:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a BigInteger
                        return SQLQueryResults.toBigInteger((Integer)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a BigInteger
                        return SQLQueryResults.toBigInteger((Long)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for long values
                case Types.BIGINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a BigInteger
                        return SQLQueryResults.toBigInteger((Long)
                                this.mData[columnIndex]);
                    } else {
                        // Cast the field value to BigInteger
                        return (BigInteger) this.mData[columnIndex];
                    }
                // Identifies generic SQL type for float values
                case Types.REAL:
                    // Converts the field value to a BigInteger
                    return SQLQueryResults.toBigInteger((Float)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for double values
                case Types.FLOAT: case Types.DOUBLE:
                    // Converts the field value to a BigInteger
                    return SQLQueryResults.toBigInteger((Double)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for string values
                case Types.VARCHAR: case Types.LONGVARCHAR:
                case Types.NVARCHAR: case Types.LONGNVARCHAR:
                    // Converts the field value to a BigInteger
                    return NumberUtils.createBigInteger(this.
                            getString(columnIndex));
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            BigInteger.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Character getCharacter(final int columnIndex) {
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Check the data type of the field specified
            switch (field.mSQLType) {
                // Identifies generic SQL type for characters
                case Types.CHAR: case Types.NCHAR:
                    // Cast the field value to Character
                    return (Character) this.mData[columnIndex];
                // Identifies generic SQL type for byte values
                case Types.TINYINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Character
                        return SQLQueryResults.toCharacter((Byte)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Character
                        return SQLQueryResults.toCharacter((Short)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for string values
                case Types.LONGNVARCHAR: case Types.VARCHAR:
                case Types.LONGVARCHAR: case Types.NVARCHAR:
                    // Return the first letter of the textual
                    // representation of the current value
                    return SQLQueryResults.getFirstChar(
                            this.mData[columnIndex]);
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            Character.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Integer getInteger(final int columnIndex) {
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Check the data type of the field specified
            switch (field.mSQLType) {
                // Identifies generic SQL type for integer values
                case Types.INTEGER:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Cast the field value to Integer
                        return (Integer) this.mData[columnIndex];
                    } else {
                        // Converts the field value to a Integer
                    return SQLQueryResults.toInteger((Long)
                            this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for boolean values
                case Types.BIT: case Types.BOOLEAN:
                    // Converts the field value to a Integer
                    return SQLQueryResults.toInteger((Boolean)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for byte values
                case Types.TINYINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Integer
                        return SQLQueryResults.toInteger((Byte)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Integer
                        return SQLQueryResults.toInteger((Short)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for short values
                case Types.SMALLINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Integer
                        return SQLQueryResults.toInteger((Short)
                                this.mData[columnIndex]);
                    } else {
                        // Cast the field value to Integer
                        return (Integer) this.mData[columnIndex];
                    }
                // Identifies generic SQL type for long values
                case Types.BIGINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Integer
                        return SQLQueryResults.toInteger((Long)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Integer
                        return SQLQueryResults.toInteger((BigInteger)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for big-decimal values
                case Types.NUMERIC: case Types.DECIMAL:
                    // Converts the field value to a Integer
                    return SQLQueryResults.toInteger((BigDecimal)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for float values
                case Types.REAL:
                    // Converts the field value to a Integer
                    return SQLQueryResults.toInteger((Float)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for double values
                case Types.FLOAT: case Types.DOUBLE:
                    // Converts the field value to a Integer
                    return SQLQueryResults.toInteger((Double)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for string values
                case Types.VARCHAR: case Types.LONGVARCHAR:
                case Types.NVARCHAR: case Types.LONGNVARCHAR:
                    // Converts the field value to a Integer
                    return NumberUtils.createInteger(Objects.toString(
                            this.mData[columnIndex], null));
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            Integer.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Boolean getBoolean(final int columnIndex) {
            // Declares a support reference
            final Object value;
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Gets the value of the requested field
            value = this.mData[columnIndex];
            // Check if this value is not null
            if (value != null) {
                // Check the data type of the field specified
                switch (field.mSQLType) {
                    // Identifies generic SQL types for boolean values
                    case Types.BIT: case Types.BOOLEAN:
                        // Cast the boolean value
                        return (Boolean) value;
                    // Identifies generic SQL type for byte values
                    case Types.TINYINT:
                        // Check if the field has the sign
                        if (field.mSigned) {
                            // Check if the value is other than zero
                            return !NumberUtils.BYTE_ZERO.equals(value);
                        } else {
                            // Check if the value is other than zero
                            return !NumberUtils.SHORT_ZERO.equals(value);
                        }
                    // Identifies generic SQL type for short values
                    case Types.SMALLINT:
                        // Check if the field has the sign
                        if (field.mSigned) {
                            // Check if the value is other than zero
                            return !NumberUtils.SHORT_ZERO.equals(value);
                        } else {
                            // Check if the value is other than zero
                            return !NumberUtils.INTEGER_ZERO.equals(value);
                        }
                    // Identifies generic SQL type for integer values
                    case Types.INTEGER:
                        // Check if the field has the sign
                        if (field.mSigned) {
                            // Check if the value is other than zero
                            return !NumberUtils.INTEGER_ZERO.equals(value);
                        } else {
                            // Check if the value is other than zero
                            return !NumberUtils.LONG_ZERO.equals(value);
                        }
                    // Identifies generic SQL type for long values
                    case Types.BIGINT:
                        // Check if the field has the sign
                        if (field.mSigned) {
                            // Check if the value is other than zero
                            return !NumberUtils.LONG_ZERO.equals(value);
                        } else {
                            // Check if the value is other than zero
                            return !BigInteger.ZERO.equals(value);
                        }
                    // Identifies generic SQL types for big-decimal values
                    case Types.NUMERIC: case Types.DECIMAL:
                        // Check if the value is other than zero
                        return !BigDecimal.ZERO.equals(value);
                    // Identifies generic SQL type for float values
                    case Types.REAL:
                        // Check if the value is other than zero
                        return !NumberUtils.FLOAT_ZERO.equals(value);
                    // Identifies generic SQL types for double values
                    case Types.FLOAT: case Types.DOUBLE:
                        // Check if the value is other than zero
                        return !NumberUtils.DOUBLE_ZERO.equals(value);
                    // Identifies generic SQL types for string values
                    case Types.VARCHAR: case Types.LONGVARCHAR:
                    case Types.NVARCHAR: case Types.LONGNVARCHAR:
                        // Converts the field value to a Boolean
                        return BooleanUtils.toBooleanObject(value.toString());
                    // For all other SQL types...
                    default:
                        // Raises an exception indicating that
                        // the current value cannot be converted
                        throw new ClassCastException(String.format(
                                SQLQueryResults.ExCannotConvert, this.
                                mOwner.mFields[columnIndex].mCls,
                                Boolean.class));
                }
            } else {
                // Return null value
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final LocalDate getDate(final int columnIndex) {
            // Check the data type of the field specified
            switch (this.mOwner.mFields[columnIndex].mSQLType) {
                // Identifies the generic SQL type DATE
                case Types.DATE:
                    // Cast the field value to LocalDate
                    return (LocalDate) this.mData[columnIndex];
                // Identifies the generic SQL type
                // TIMESTAMP (with or without time zone)
                case Types.TIMESTAMP: case Types.TIMESTAMP_WITH_TIMEZONE:
                    // Converts the field value to a LocalDate
                    return SQLQueryResults.toLocalDate((TemporalAccessor)
                            this.mData[columnIndex]);
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            LocalDate.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final LocalTime getTime(final int columnIndex) {
            // Check the data type of the field specified
            switch (this.mOwner.mFields[columnIndex].mSQLType) {
                case Types.TIME:
                    // Cast the field value to LocalTime
                    return (LocalTime) this.mData[columnIndex];
                // Identifies the generic SQL type
                // TIMESTAMP (with or without time zone)
                case Types.TIMESTAMP: case Types.TIMESTAMP_WITH_TIMEZONE:
                    // Converts the field value to a LocalTime
                    return SQLQueryResults.toLocalTime((TemporalAccessor)
                            this.mData[columnIndex]);
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            LocalTime.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final String getString(final int columnIndex) {
            // Gets the textual representation of the field value
            return Objects.toString(this.mData[columnIndex], null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final byte[] getBytes(final int columnIndex) {
            // Check the data type of the field specified
            switch (this.mOwner.mFields[columnIndex].mSQLType) {
                // Identifies generic SQL types for binary fields
                case Types.VARBINARY: case Types.BINARY:
                case Types.BLOB: case Types.LONGVARBINARY:
                    // Cast the field value to a byte array
                    return (byte[]) this.mData[columnIndex];
                // Identifies generic SQL types for string values
                case Types.LONGNVARCHAR: case Types.VARCHAR:
                case Types.LONGVARCHAR: case Types.NVARCHAR:
                    // Return the bytes of the text
                    // representation of the field value
                    return SQLQueryResults.toBytesFromString(
                            this.mData[columnIndex]);
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            byte[].class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Double getDouble(final int columnIndex) {
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Check the data type of the field specified
            switch (field.mSQLType) {
                // Identifies generic SQL types for double values
                case Types.FLOAT: case Types.DOUBLE:
                    // Cast the field value to Double
                    return (Double) this.mData[columnIndex];
                // Identifies generic SQL types for boolean values
                case Types.BIT: case Types.BOOLEAN:
                    // Converts the field value to a Double
                    return SQLQueryResults.toDouble((Boolean)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for byte values
                case Types.TINYINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Double
                        return SQLQueryResults.toDouble((Byte)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Double
                        return SQLQueryResults.toDouble((Short)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for short values
                case Types.SMALLINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Double
                        return SQLQueryResults.toDouble((Short)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Double
                        return SQLQueryResults.toDouble((Integer)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for integer values
                case Types.INTEGER:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Double
                        return SQLQueryResults.toDouble((Integer)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Double
                        return SQLQueryResults.toDouble((Long)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for long values
                case Types.BIGINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Double
                        return SQLQueryResults.toDouble((Long)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Double
                        return SQLQueryResults.toDouble((BigInteger)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for big-decimal values
                case Types.NUMERIC: case Types.DECIMAL:
                    // Converts the field value to a Double
                    return SQLQueryResults.toDouble((BigDecimal)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for float values
                case Types.REAL:
                    // Converts the field value to a Double
                    return SQLQueryResults.toDouble((Float)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for string values
                case Types.VARCHAR: case Types.LONGVARCHAR:
                case Types.NVARCHAR: case Types.LONGNVARCHAR:
                    // Converts the field value to a Double
                    return NumberUtils.createDouble(Objects.toString(
                            this.mData[columnIndex], null));
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            Double.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Float getFloat(final int columnIndex) {
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Check the data type of the field specified
            switch (field.mSQLType) {
                // Identifies generic SQL type for float values
                case Types.REAL:
                    // Cast the field value to Float
                    return (Float) this.mData[columnIndex];
                // Identifies generic SQL types for boolean values
                case Types.BIT: case Types.BOOLEAN:
                    // Converts the field value to a Float
                    return SQLQueryResults.toFloat((Boolean)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for byte values
                case Types.TINYINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Float
                        return SQLQueryResults.toFloat((Byte)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Float
                        return SQLQueryResults.toFloat((Short)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for short values
                case Types.SMALLINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Float
                        return SQLQueryResults.toFloat((Short)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Float
                        return SQLQueryResults.toFloat((Integer)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for integer values
                case Types.INTEGER:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Float
                        return SQLQueryResults.toFloat((Integer)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Float
                        return SQLQueryResults.toFloat((Long)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for long values
                case Types.BIGINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Float
                        return SQLQueryResults.toFloat((Long)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Float
                        return SQLQueryResults.toFloat((BigInteger)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for big-decimal values
                case Types.NUMERIC: case Types.DECIMAL:
                    // Converts the field value to a Float
                    return SQLQueryResults.toFloat((BigDecimal)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for double values
                case Types.FLOAT: case Types.DOUBLE:
                    // Converts the field value to a Float
                    return SQLQueryResults.toFloat((Double)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for string values
                case Types.VARCHAR: case Types.LONGVARCHAR:
                case Types.NVARCHAR: case Types.LONGNVARCHAR:
                    // Converts the field value to a Float
                    return NumberUtils.createFloat(Objects.toString(
                            this.mData[columnIndex], null));
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            Float.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Short getShort(final int columnIndex) {
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Check the data type of the field specified
            switch (field.mSQLType) {
                // Identifies generic SQL type for short values
                case Types.SMALLINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Cast the field value to Short
                        return (Short) this.mData[columnIndex];
                    } else {
                        // Converts the field value to a Short
                        return SQLQueryResults.toShort((Integer)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for boolean values
                case Types.BIT: case Types.BOOLEAN:
                    // Converts the field value to a Short
                    return SQLQueryResults.toShort((Boolean)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for byte values
                case Types.TINYINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Short
                        return SQLQueryResults.toShort((Byte)
                                this.mData[columnIndex]);
                    } else {
                        // Cast the field value to Short
                        return (Short) this.mData[columnIndex];
                    }
                // Identifies generic SQL type for integer values
                case Types.INTEGER:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Short
                        return SQLQueryResults.toShort((Integer)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Short
                        return SQLQueryResults.toShort((Long)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for long values
                case Types.BIGINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Short
                        return SQLQueryResults.toShort((Long)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Short
                        return SQLQueryResults.toShort((BigInteger)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for big-decimal values
                case Types.NUMERIC: case Types.DECIMAL:
                    // Converts the field value to a Short
                    return SQLQueryResults.toShort((BigDecimal)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for float values
                case Types.REAL:
                    // Converts the field value to a Short
                    return SQLQueryResults.toShort((Float)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for double values
                case Types.FLOAT: case Types.DOUBLE:
                    // Converts the field value to a Short
                    return SQLQueryResults.toShort((Double)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for string values
                case Types.VARCHAR: case Types.LONGVARCHAR:
                case Types.NVARCHAR: case Types.LONGNVARCHAR:
                    // Converts the field value to a Short
                    return SQLQueryResults.toShortFromStr(
                            this.mData[columnIndex]);
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            Short.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Long getLong(final int columnIndex) {
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Check the data type of the field specified
            switch (field.mSQLType) {
                // Identifies generic SQL type for long values
                case Types.BIGINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Cast the field value to Long
                        return (Long) this.mData[columnIndex];
                    } else {
                        // Converts the field value to a Long
                        return SQLQueryResults.toLong((BigInteger)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for boolean values
                case Types.BIT: case Types.BOOLEAN:
                    // Converts the field value to a Long
                    return SQLQueryResults.toLong((Boolean)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for byte values
                case Types.TINYINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Long
                        return SQLQueryResults.toLong((Byte)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Long
                        return SQLQueryResults.toLong((Short)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for short values
                case Types.SMALLINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Long
                        return SQLQueryResults.toLong((Short)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Long
                        return SQLQueryResults.toLong((Integer)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for integer values
                case Types.INTEGER:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Long
                        return SQLQueryResults.toLong((Integer)
                                this.mData[columnIndex]);
                    } else {
                        // Cast the field value to Long
                        return (Long) this.mData[columnIndex];
                    }
                // Identifies generic SQL types for big-decimal values
                case Types.NUMERIC: case Types.DECIMAL:
                    // Converts the field value to a Long
                    return SQLQueryResults.toLong((BigDecimal)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for float values
                case Types.REAL:
                    // Converts the field value to a Long
                    return SQLQueryResults.toLong((Float)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for double values
                case Types.FLOAT: case Types.DOUBLE:
                    // Converts the field value to a Long
                    return SQLQueryResults.toLong((Double)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for string values
                case Types.VARCHAR: case Types.LONGVARCHAR:
                case Types.NVARCHAR: case Types.LONGNVARCHAR:
                    // Converts the field value to a Long
                    return NumberUtils.createLong(Objects.toString(
                            this.mData[columnIndex], null));
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            Long.class));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Byte getByte(final int columnIndex) {
            // Declares a support reference
            final SQLQueryResults.Field field;
            // Get the info to the required field
            field = this.mOwner.mFields[columnIndex];
            // Check the data type of the field specified
            switch (field.mSQLType) {
                // Identifies generic SQL type for byte values
                case Types.TINYINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Cast the field value to Byte
                        return (Byte) this.mData[columnIndex];
                    } else {
                        // Converts the field value to a Byte
                        return SQLQueryResults.toByte((Short)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for boolean values
                case Types.BIT: case Types.BOOLEAN:
                    // Converts the field value to a Byte
                    return SQLQueryResults.toByte((Boolean)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for short values
                case Types.SMALLINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Byte
                        return SQLQueryResults.toByte((Short)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Byte
                        return SQLQueryResults.toByte((Integer)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for integer values
                case Types.INTEGER:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Byte
                        return SQLQueryResults.toByte((Integer)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Byte
                        return SQLQueryResults.toByte((Long)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL type for long values
                case Types.BIGINT:
                    // Check if the field has the sign
                    if (field.mSigned) {
                        // Converts the field value to a Byte
                        return SQLQueryResults.toByte((Long)
                                this.mData[columnIndex]);
                    } else {
                        // Converts the field value to a Byte
                        return SQLQueryResults.toByte((BigInteger)
                                this.mData[columnIndex]);
                    }
                // Identifies generic SQL types for big-decimal values
                case Types.NUMERIC: case Types.DECIMAL:
                    // Converts the field value to a Byte
                    return SQLQueryResults.toByte((BigDecimal)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for float values
                case Types.REAL:
                    // Converts the field value to a Byte
                    return SQLQueryResults.toByte((Float)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for double values
                case Types.FLOAT: case Types.DOUBLE:
                    // Converts the field value to a Byte
                    return SQLQueryResults.toByte((Double)
                            this.mData[columnIndex]);
                // Identifies generic SQL type for characters
                case Types.CHAR: case Types.NCHAR:
                    // Converts the field value to a Byte
                    return SQLQueryResults.toByte((Character)
                            this.mData[columnIndex]);
                // Identifies generic SQL types for string values
                case Types.VARCHAR: case Types.LONGVARCHAR:
                case Types.NVARCHAR: case Types.LONGNVARCHAR:
                    // Converts the field value to a Byte
                    return SQLQueryResults.toByteFromStr(
                            this.mData[columnIndex]);
                // For all other SQL types...
                default:
                    // Raises an exception indicating that
                    // the current value cannot be converted
                    throw new ClassCastException(String.format(
                            SQLQueryResults.ExCannotConvert, this.
                            mOwner.mFields[columnIndex].mCls,
                            Byte.class));
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final <T> T getObject(final Class<T> cls, final String columnName) {
            // Invokes the method that uses the field index
            return this.getObject(cls, this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final <T> T getObject(final Class<T> cls, final int columnIndex) {
            // Perform the cast on the field value
            return cls.cast(this.mData[columnIndex]);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final LocalDateTime getTimestamp(final String columnName) {
            // Invokes the method that uses the field index
            return this.getTimestamp(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final BigDecimal getBigDecimal(final String columnName) {
            // Invokes the method that uses the field index
            return this.getBigDecimal(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final BigInteger getBigInteger(final String columnName) {
            // Invokes the method that uses the field index
            return this.getBigInteger(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Class<?> getFieldClass(final String columnName) {
            // Invokes the method provided by the QueryResult
            return this.mOwner.getFieldClass(columnName);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Character getCharacter(final String columnName) {
            // Invokes the method that uses the field index
            return this.getCharacter(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Class<?> getFieldClass(final int columnIndex) {
            // Returns the Java class for the specified field
            return this.mOwner.mFields[columnIndex].mCls;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Boolean getBoolean(final String columnName) {
            // Invokes the method that uses the field index
            return this.getBoolean(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Integer getInteger(final String columnName) {
            // Invokes the method that uses the field index
            return this.getInteger(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final int getFieldIndex(final String columnName) {
            // Invokes the method that uses the field index
            return this.mOwner.getFieldIndex(columnName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final String getFieldName(final int columnIndex) {
            // Invokes the method that uses the field index
            return this.mOwner.mFields[columnIndex].mName;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final LocalTime getTime(final String columnName) {
            // Invokes the method that uses the field index
            return this.getTime(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final LocalDate getDate(final String columnName) {
            // Invokes the method that uses the field index
            return this.getDate(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Object getObject(final String columnName) {
            // Invokes the method that uses the field index
            return this.getObject(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Double getDouble(final String columnName) {
            // Invokes the method that uses the field index
            return this.getDouble(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final String getString(final String columnName) {
            // Invokes the method that uses the field index
            return this.getString(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final byte[] getBytes(final String columnName) {
            // Invokes the method that uses the field index
            return this.getBytes(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final boolean isSigned(final String fieldName) {
            // Invokes the method that uses the field index
            return this.isSigned(this.getFieldIndex(fieldName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Short getShort(final String columnName) {
            // Invokes the method that uses the field index
            return this.getShort(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Float getFloat(final String columnName) {
            // Invokes the method that uses the field index
            return this.getFloat(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final boolean isSigned(final int fieldIndex) {
            // Returns the sign flag of the i-th field
            return this.mOwner.mFields[fieldIndex].mSigned;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Long getLong(final String columnName) {
            // Invokes the method that uses the field index
            return this.getLong(this.getFieldIndex(columnName));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Byte getByte(final String columnName) {
            // Invokes the method that uses the field index
            return this.getByte(this.getFieldIndex(columnName));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final int getFieldCount() {
            // Returns the number of fields
            // that make up the query results
            return this.mOwner.mColumnCount;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final String toString() {
            // Return the text representation
            // of the array of record values
            return Arrays.toString(this.mData);
        }
    }
    
    /**
     * Internal procedure that maps each SQL
     * type into the respective Java class.
     * 
     * @param sqlType SQL type code to convert.
     * @param signed indicates if the field held signed numbers.
     * @return Java class for the specified SQL type.
     */
    private static Class<?> mapSQLType(final int
            sqlType, final boolean signed) {
        // Check the SQL type provided
        switch (sqlType) {
            // Identifies generic SQL types for boolean values
            case Types.BIT: case Types.BOOLEAN:
                // Return the class of Boolean values
                return Boolean.class;
            // Identifies generic SQL type for integer values
            case Types.INTEGER:
                // Return the Integer class for values with
                // the sign or Long for unsigned values
                return signed ? Integer.class : Long.class;
            // Identifies generic SQL types for string values
            case Types.VARCHAR: case Types.LONGVARCHAR:
            case Types.NVARCHAR: case Types.LONGNVARCHAR:
                // Return the class of String values
                return String.class;
            // Identifies generic SQL types for double values
            case Types.FLOAT: case Types.DOUBLE:
                // Return the class of Double values
                return Double.class;
            // Identifies generic SQL type for float values
            case Types.REAL:
                // Return the class of Float values
                return Float.class;
            // Identifies generic SQL type for byte values
            case Types.TINYINT:
                // Return the Byte class for values with
                // the sign or Short for unsigned values
                return signed ? Byte.class : Short.class;
            // Identifies generic SQL type for short values
            case Types.SMALLINT:
                // Return the Short class for values with
                // the sign or Integer for unsigned values
                return signed ? Short.class : Integer.class;
            // Identifies generic SQL type for characters
            case Types.CHAR: case Types.NCHAR:
                // Return the class of Character values
                return Character.class;
            // Identifies generic SQL type for long values
            case Types.BIGINT:
                // Return the Long class for values with
                // the sign or BigInteger for unsigned values
                return signed ? Long.class : BigInteger.class;
            // Identifies generic SQL types for big-decimal values
            case Types.DECIMAL: case Types.NUMERIC:
                // Return the class of BigDecimal values
                return BigDecimal.class;
            // Identifies the generic SQL type DATE
            case Types.DATE:
                // Return the class of LocalDate values
                return LocalDate.class;
            // Identifies the generic SQL type TIME
            case Types.TIME:
                // Return the class of LocalTime values
                return LocalTime.class;
            // Identifies the generic SQL type TIMESTAMP
            case Types.TIMESTAMP:
                // Return the class of LocalDateTime values
                return LocalDateTime.class;
            // Identifies generic SQL types for binary fields
            case Types.VARBINARY: case Types.BINARY:
            case Types.BLOB: case Types.LONGVARBINARY:
                // Return the class for byte arrays
                return byte[].class;
            // Identifies the generic SQL type TIMESTAMP with timezone
            case Types.TIMESTAMP_WITH_TIMEZONE:
                // Return the class of OffsetDateTime values
                return OffsetDateTime.class;
            // Identifies the generic SQL type TIME with timezone
            case Types.TIME_WITH_TIMEZONE:
                // Return the class of OffsetTime values
                return OffsetTime.class;
            // Identifies the generic SQL type NULL
            case Types.NULL:
                // Return the Void class reference
                return Void.class;
            // For all other SQL types...
            default:
                // Return the Object class reference
                return Object.class;
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert a temporal values.">
    /**
     * Internal utility method that returns a {@link
     * LocalDateTime} starting from a {@link OffsetDateTime}.
     *
     * @param value {@link OffsetDateTime} value to be converted.
     * @return {@link LocalDateTime} corresponding to the value provided.
     */
    private static LocalDateTime toLocalDateTime(final OffsetDateTime value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.toLocalDateTime();
        } else {
            // Return null value
            return null;
        }
    }

    /**
     * Internal utility method that returns a {@link
     * LocalDateTime} set at midnight for a certain {@link LocalDate}.
     *
     * @param value {@link LocalDate} value to be converted.
     * @return {@link LocalDateTime} corresponding to the value provided.
     */
    private static LocalDateTime atMidnight(final LocalDate value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.atStartOfDay();
        } else {
            // Return null value
            return null;
        }
    }

    /**
     * Internal utility method that returns a {@link
     * LocalDate} starting from a {@link TemporalAccessor}.
     *
     * @param value {@link TemporalAccessor} value to be converted.
     * @return {@link LocalDate} corresponding to the value provided.
     */
    private static LocalDate toLocalDate(final TemporalAccessor value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return LocalDate.from(value);
        } else {
            // Return null value
            return null;
        }
    }

    /**
     * Internal utility method that returns a {@link
     * LocalTime} starting from a {@link TemporalAccessor}.
     *
     * @param value {@link TemporalAccessor} value to be converted.
     * @return {@link LocalTime} corresponding to the value provided.
     */
    private static LocalTime toLocalTime(final TemporalAccessor value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return LocalTime.from(value);
        } else {
            // Return null value
            return null;
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Internal utility methods for managing byte streams.">
    /**
     * Internal procedure that reads the content of
     * a certain binary field from the current record.
     * 
     * @param rs {@link ResultSet} pointing to the current record.
     * @param field reference to the field to be read.
     * @param buffer reference to the copy buffer.
     * @return binary content of the specified field.
     * @throws SQLException if errors occur during reading.
     */
    private static byte[] readBytes(final ResultSet rs, final SQLQueryResults.
            Field field, final Mutable<byte[]> buffer) throws SQLException {
        // Retrieves the i-th binary field from the current record
        try (final InputStream stream = rs.getBinaryStream(field.mIndex + 1)) {
            // Check that it is not null
            if (stream != null) {
                // Copy the contents of the binary stream
                return SQLQueryResults.copyFromStream(stream, buffer);
            } else {
                // Return null value
                return null;
            }
        }
        // Capture every exception of reading the stream
        catch (final IOException ex) {
            // Raises an exception reporting the problem
            // encountered when reading a certain binary field
            throw new SQLException(String.format(SQLQueryResults.
                    ExReadingBinaryField, field.mName), ex);
        }
    }
    
    /**
     * Internal procedure that copies the binary content of a certain stream.
     * <p>The {@code byteRef} parameter provides an indirect reference to the
     * copy buffer. Through this indirect reference it is possible to increase
     * the capacity of the current buffer, to reduce the number of read
     * iterations to the next call to this method.</p>
     * 
     * @param input stream from which to copy the content.
     * @param bufferRef reference to the buffer used for copying.
     * @return binary content of the stream provided.
     * @throws IOException if errors occur while reading the stream.
     */
    private static byte[] copyFromStream(final InputStream input,
            final Mutable<byte[]> bufferRef) throws IOException {
        // Declares a support array
        byte[] buffer;
        // Declares three support integers
        int c, off, len;
        // Gets the reference to the binary buffer
        buffer = bufferRef.getValue();
        // Check if the buffer still needs to be allocated
        if (buffer == null) {
            // Allocates a certain initial byte buffer
            buffer = new byte[SQLQueryResults.BufferSize];
            // Overwrites the buffer reference
            bufferRef.setValue(buffer);
        }
        // Start an infinite loop
        for (off = 0; true; off += c) {
            // Calculates the number of residual bytes
            // that can be read through the current buffer
            len = buffer.length - off;
            // Reads a certain number of bytes from the stream
            c = input.read(buffer, off, len);
            // Check if the stream has been completely read
            if (c < len) {
                // Calculate the total number of bytes read
                len = off + Math.max(c, 0);
                // Returns the contents of the buffer
                return Arrays.copyOf(buffer, len);
            } else {
                // Double the buffer capacity
                buffer = Arrays.copyOf(buffer, buffer.length << 1);
                // Overwrites the buffer reference
                bufferRef.setValue(buffer);
            }
        }
    }
    
    /**
     * Internal utility method that returns the text
     * representation of a certain object as a byte array.
     * 
     * @param obj object from which to get the byte array.
     * @return byte array derived from the textual
     *         representation of the supplied object.
     */
    private static byte[] toBytesFromString(final Object obj) {
        // Gets the textual representation of the supplied object
        final String str = Objects.toString(obj, null);
        // Check that this representation is not null
        if (str != null) {
            // Return this string as an array of bytes
            return str.getBytes();
        } else {
            // Return null value
            return null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert a BigDecimal.">
    /**
     * Internal utility method that returns a
     * {@link BigDecimal} starting from a {@link Byte}.
     *
     * @param value {@link Byte} value to be converted.
     * @return {@link BigDecimal} corresponding to the value provided.
     */
    private static BigDecimal toBigDecimal(final Byte value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return new BigDecimal(value.intValue());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigDecimal} starting from a {@link Short}.
     *
     * @param value {@link Short} value to be converted.
     * @return {@link BigDecimal} corresponding to the value provided.
     */
    private static BigDecimal toBigDecimal(final Short value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return new BigDecimal(value.intValue());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigDecimal} starting from a {@link Integer}.
     *
     * @param value {@link Integer} value to be converted.
     * @return {@link BigDecimal} corresponding to the value provided.
     */
    private static BigDecimal toBigDecimal(final Integer value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return new BigDecimal(value);
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigDecimal} starting from a {@link Float}.
     *
     * @param value {@link Float} value to be converted.
     * @return {@link BigDecimal} corresponding to the value provided.
     */
    private static BigDecimal toBigDecimal(final Float value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return new BigDecimal(value.doubleValue());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigDecimal} starting from a {@link Long}.
     *
     * @param value {@link Long} value to be converted.
     * @return {@link BigDecimal} corresponding to the value provided.
     */
    private static BigDecimal toBigDecimal(final Long value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return new BigDecimal(value);
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a {@link
     * BigDecimal} starting from a {@link BigInteger}.
     *
     * @param value {@link BigInteger} value to be converted.
     * @return {@link BigDecimal} corresponding to the value provided.
     */
    private static BigDecimal toBigDecimal(final BigInteger value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return new BigDecimal(value);
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigDecimal} starting from a {@link Double}.
     *
     * @param value {@link Double} value to be converted.
     * @return {@link BigDecimal} corresponding to the value provided.
     */
    private static BigDecimal toBigDecimal(final Double value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return new BigDecimal(value);
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigDecimal} starting from a {@link Boolean}.
     *
     * @param value {@link Boolean} value to be converted.
     * @return {@link BigDecimal} corresponding to the value provided.
     */
    private static BigDecimal toBigDecimal(final Boolean value) {
        // Check if the value is not null
        if (value != null) {
            // Check the value provided
            if (value) {
                // Return the value one
                return BigDecimal.ONE;
            } else {
                // Return the value zero
                return BigDecimal.ZERO;
            }
        } else {
            // Return null value
            return null;
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert a BigInteger.">
    /**
     * Internal utility method that returns a
     * {@link BigInteger} starting from a {@link Byte}.
     *
     * @param value {@link Byte} value to be converted.
     * @return {@link BigInteger} corresponding to the value provided.
     */
    private static BigInteger toBigInteger(final Byte value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return BigInteger.valueOf(value.longValue());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigInteger} starting from a {@link Short}.
     *
     * @param value {@link Short} value to be converted.
     * @return {@link BigInteger} corresponding to the value provided.
     */
    private static BigInteger toBigInteger(final Short value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return BigInteger.valueOf(value.longValue());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigInteger} starting from a {@link Integer}.
     *
     * @param value {@link Integer} value to be converted.
     * @return {@link BigInteger} corresponding to the value provided.
     */
    private static BigInteger toBigInteger(final Integer value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return BigInteger.valueOf(value.longValue());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigInteger} starting from a {@link Float}.
     *
     * @param value {@link Float} value to be converted.
     * @return {@link BigInteger} corresponding to the value provided.
     */
    private static BigInteger toBigInteger(final Float value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return BigInteger.valueOf(value.longValue());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigInteger} starting from a {@link Long}.
     *
     * @param value {@link Long} value to be converted.
     * @return {@link BigInteger} corresponding to the value provided.
     */
    private static BigInteger toBigInteger(final Long value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return BigInteger.valueOf(value);
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a {@link
     * BigInteger} starting from a {@link BigDecimal}.
     *
     * @param value {@link BigDecimal} value to be converted.
     * @return {@link BigInteger} corresponding to the value provided.
     */
    private static BigInteger toBigInteger(final BigDecimal value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.toBigInteger();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigInteger} starting from a {@link Double}.
     *
     * @param value {@link Double} value to be converted.
     * @return {@link BigInteger} corresponding to the value provided.
     */
    private static BigInteger toBigInteger(final Double value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return BigInteger.valueOf(value.longValue());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link BigInteger} starting from a {@link Boolean}.
     *
     * @param value {@link Boolean} value to be converted.
     * @return {@link BigInteger} corresponding to the value provided.
     */
    private static BigInteger toBigInteger(final Boolean value) {
        // Check if the value is not null
        if (value != null) {
            // Check the value provided
            if (value) {
                // Return the value one
                return BigInteger.ONE;
            } else {
                // Return the value zero
                return BigInteger.ZERO;
            }
        } else {
            // Return null value
            return null;
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert a Character.">
    /**
     * Internal utility method that returns a
     * {@link Character} starting from a {@link Byte}.
     *
     * @param value {@link Byte} value to be converted.
     * @return {@link Character} corresponding to the value provided.
     */
    private static Character toCharacter(final Byte value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return (char) value.byteValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Character} starting from a {@link Short}.
     *
     * @param value {@link Short} value to be converted.
     * @return {@link Character} corresponding to the value provided.
     */
    private static Character toCharacter(final Short value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return (char) value.shortValue();
        } else {
            // Return null value
            return null;
        }
    }

    /**
     * Internal utility method that returns the first
     * character of the text representation of a certain value.
     * 
     * @param value value from which to get the character.
     * @return first character of the textual representation
     *         of the object supplied.
     */
    private static Character getFirstChar(final Object value) {
        // Gets the textual representation of the supplied value
        final String str = Objects.toString(value, null);
        // Check that this representation is not null or empty
        if (str != null && str.length() > 0) {
            // Return the first character of the string
            return (char) str.charAt(0);
        } else {
            // Return null value
            return null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert an Integer.">
    /**
     * Internal utility method that returns a
     * {@link Integer} starting from a {@link Byte}.
     *
     * @param value {@link Byte} value to be converted.
     * @return {@link Integer} corresponding to the value provided.
     */
    private static Integer toInteger(final Byte value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.intValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Integer} starting from a {@link Short}.
     *
     * @param value {@link Short} value to be converted.
     * @return {@link Integer} corresponding to the value provided.
     */
    private static Integer toInteger(final Short value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.intValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Integer} starting from a {@link Long}.
     *
     * @param value {@link Long} value to be converted.
     * @return {@link Integer} corresponding to the value provided.
     */
    private static Integer toInteger(final Long value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the integer limits
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Integer.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.intValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Integer} starting from a {@link Float}.
     *
     * @param value {@link Float} value to be converted.
     * @return {@link Integer} corresponding to the value provided.
     */
    private static Integer toInteger(final Float value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the integer limits
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Integer.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.intValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Integer} starting from a {@link Double}.
     *
     * @param value {@link Double} value to be converted.
     * @return {@link Integer} corresponding to the value provided.
     */
    private static Integer toInteger(final Double value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the integer limits
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Integer.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.intValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Integer} starting from a {@link BigDecimal}.
     *
     * @param value {@link BigDecimal} value to be converted.
     * @return {@link Integer} corresponding to the value provided.
     */
    private static Integer toInteger(final BigDecimal value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into an integer
            return value.intValueExact();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Integer} starting from a {@link BigInteger}.
     *
     * @param value {@link BigInteger} value to be converted.
     * @return {@link Integer} corresponding to the value provided.
     */
    private static Integer toInteger(final BigInteger value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into an integer
            return value.intValueExact();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Integer} starting from a {@link Boolean}.
     *
     * @param value {@link Boolean} value to be converted.
     * @return {@link Integer} corresponding to the value provided.
     */
    private static Integer toInteger(final Boolean value) {
        // Check if the value is not null
        if (value != null) {
            // Check the value provided
            if (value) {
                // Return the value one
                return NumberUtils.INTEGER_ONE;
            } else {
                // Return the value zero
                return NumberUtils.INTEGER_ZERO;
            }
        } else {
            // Return null value
            return null;
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert a Double.">
    /**
     * Internal utility method that returns a
     * {@link Double} starting from a {@link Byte}.
     *
     * @param value {@link Byte} value to be converted.
     * @return {@link Double} corresponding to the value provided.
     */
    private static Double toDouble(final Byte value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.doubleValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Double} starting from a {@link Short}.
     *
     * @param value {@link Short} value to be converted.
     * @return {@link Double} corresponding to the value provided.
     */
    private static Double toDouble(final Short value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.doubleValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Double} starting from a {@link Integer}.
     *
     * @param value {@link Integer} value to be converted.
     * @return {@link Double} corresponding to the value provided.
     */
    private static Double toDouble(final Integer value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.doubleValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Double} starting from a {@link Float}.
     *
     * @param value {@link Float} value to be converted.
     * @return {@link Double} corresponding to the value provided.
     */
    private static Double toDouble(final Float value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.doubleValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Double} starting from a {@link Long}.
     *
     * @param value {@link Long} value to be converted.
     * @return {@link Double} corresponding to the value provided.
     */
    private static Double toDouble(final Long value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.doubleValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Double} starting from a {@link BigInteger}.
     *
     * @param value {@link BigInteger} value to be converted.
     * @return {@link Double} corresponding to the value provided.
     */
    private static Double toDouble(final BigInteger value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a double value
            return value.doubleValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Double} starting from a {@link BigDecimal}.
     *
     * @param value {@link BigDecimal} value to be converted.
     * @return {@link Double} corresponding to the value provided.
     */
    private static Double toDouble(final BigDecimal value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a double value
            return value.doubleValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Double} starting from a {@link Boolean}.
     *
     * @param value {@link Boolean} value to be converted.
     * @return {@link Double} corresponding to the value provided.
     */
    private static Double toDouble(final Boolean value) {
        // Check if the value is not null
        if (value != null) {
            // Check the value provided
            if (value) {
                // Return the value one
                return NumberUtils.DOUBLE_ONE;
            } else {
                // Return the value zero
                return NumberUtils.DOUBLE_ZERO;
            }
        } else {
            // Return null value
            return null;
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert a Short.">
    /**
     * Internal procedure that reads an unsigned {@link java.lang.Short}
     * value from a {@link java.sql.ResultSet} record by wrapping it in
     * a {@link java.lang.Integer} value.
     * 
     * @param rs {@link ResultSet} from which to read the requested value.
     * @param i column index in which to read the value.
     * @return {@link java.lang.Integer} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    private static Integer readUnsignedShort(final ResultSet rs, final int i) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final int value = rs.getInt(i);
        // If the column is null then return null
        if (rs.wasNull()) return null;
        // Check if the value read is out of range
        if (value < 0 || value > 65535) {
            // Raises an exception indicating that an overflow has occurred
            throw new SQLDataException(String.format(SQLQueryResults.
                    ExUnsignedOverflow, value, i, rs.getMetaData().
                    getColumnTypeName(i)), SQLQueryResults.
                    SQLStateOutOfRange);
        } else {
            // Returns the value read
            return value;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Short} starting from a {@link Byte}.
     *
     * @param value {@link Byte} value to be converted.
     * @return {@link Short} corresponding to the value provided.
     */
    private static Short toShort(final Byte value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.shortValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Short} starting from a {@link Integer}.
     *
     * @param value {@link Integer} value to be converted.
     * @return {@link Short} corresponding to the value provided.
     */
    private static Short toShort(final Integer value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the short limits
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Short.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.shortValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Short} starting from a {@link Long}.
     *
     * @param value {@link Long} value to be converted.
     * @return {@link Short} corresponding to the value provided.
     */
    private static Short toShort(final Long value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the short limits
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Short.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.shortValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Short} starting from a {@link Float}.
     *
     * @param value {@link Float} value to be converted.
     * @return {@link Short} corresponding to the value provided.
     */
    private static Short toShort(final Float value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the short limits
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                throw new ArithmeticException(Byte.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.shortValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Short} starting from a {@link Double}.
     *
     * @param value {@link Double} value to be converted.
     * @return {@link Short} corresponding to the value provided.
     */
    private static Short toShort(final Double value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the short limits
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Byte.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.shortValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that converts the text
     * representation of an object into a {@link Short}.
     *
     * @param value {@link Object} value to be converted.
     * @return {@link Short} corresponding to the value provided.
     */
    private static Short toShortFromStr(final Object value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the string into a short
            return Short.valueOf(value.toString());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Short} starting from a {@link BigInteger}.
     *
     * @param value {@link BigInteger} value to be converted.
     * @return {@link Short} corresponding to the value provided.
     */
    private static Short toShort(final BigInteger value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a short
            return value.shortValueExact();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Short} starting from a {@link BigDecimal}.
     *
     * @param value {@link BigDecimal} value to be converted.
     * @return {@link Short} corresponding to the value provided.
     */
    private static Short toShort(final BigDecimal value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a short
            return value.shortValueExact();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Short} starting from a {@link Boolean}.
     *
     * @param value {@link Boolean} value to be converted.
     * @return {@link Short} corresponding to the value provided.
     */
    private static Short toShort(final Boolean value) {
        // Check if the value is not null
        if (value != null) {
            // Check the value provided
            if (value) {
                // Return the value one
                return NumberUtils.SHORT_ONE;
            } else {
                // Return the value zero
                return NumberUtils.SHORT_ZERO;
            }
        } else {
            // Return null value
            return null;
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert a Float.">
    /**
     * Internal utility method that returns a
     * {@link Float} starting from a {@link Byte}.
     *
     * @param value {@link Byte} value to be converted.
     * @return {@link Float} corresponding to the value provided.
     */
    private static Float toFloat(final Byte value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.floatValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Float} starting from a {@link Short}.
     *
     * @param value {@link Short} value to be converted.
     * @return {@link Float} corresponding to the value provided.
     */
    private static Float toFloat(final Short value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.floatValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Float} starting from a {@link Integer}.
     *
     * @param value {@link Integer} value to be converted.
     * @return {@link Float} corresponding to the value provided.
     */
    private static Float toFloat(final Integer value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.floatValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Float} starting from a {@link Long}.
     *
     * @param value {@link Long} value to be converted.
     * @return {@link Float} corresponding to the value provided.
     */
    private static Float toFloat(final Long value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.floatValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Float} starting from a {@link Double}.
     *
     * @param value {@link Double} value to be converted.
     * @return {@link Float} corresponding to the value provided.
     */
    private static Float toFloat(final Double value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the float limits
            if (value < Float.MIN_VALUE || value > Float.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Float.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.floatValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that converts the text
     * representation of an object into a {@link Float}.
     *
     * @param value {@link Object} value to be converted.
     * @return {@link Float} corresponding to the value provided.
     */
    private static Float toFloatFromStr(final Object value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the string into a float value
            return Float.valueOf(value.toString());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Float} starting from a {@link BigInteger}.
     *
     * @param value {@link BigInteger} value to be converted.
     * @return {@link Float} corresponding to the value provided.
     */
    private static Float toFloat(final BigInteger value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a float value
            return value.floatValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Float} starting from a {@link BigDecimal}.
     *
     * @param value {@link BigDecimal} value to be converted.
     * @return {@link Float} corresponding to the value provided.
     */
    private static Float toFloat(final BigDecimal value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a float value
            return value.floatValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Float} starting from a {@link Boolean}.
     *
     * @param value {@link Boolean} value to be converted.
     * @return {@link Float} corresponding to the value provided.
     */
    private static Float toFloat(final Boolean value) {
        // Check if the value is not null
        if (value != null) {
            // Check the value provided
            if (value) {
                // Return the value one
                return NumberUtils.FLOAT_ONE;
            } else {
                // Return the value zero
                return NumberUtils.FLOAT_ZERO;
            }
        } else {
            // Return null value
            return null;
        }
    }//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert a Byte.">
    /**
     * Internal procedure that reads an unsigned {@link java.lang.Byte}
     * value from a {@link java.sql.ResultSet} record by wrapping it in
     * a {@link java.lang.Short} value.
     * 
     * @param rs {@link ResultSet} from which to read the requested value.
     * @param i column index in which to read the value.
     * @return {@link java.lang.Short} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    private static Short readUnsignedByte(final ResultSet rs, final int i) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final short value = rs.getShort(i);
        // If the column is null then return null
        if (rs.wasNull()) return null;
        // Check if the value read is out of range
        if (value < 0 || value > 255) {
            // Raises an exception indicating that an overflow has occurred
            throw new SQLDataException(String.format(SQLQueryResults.
                    ExUnsignedOverflow, value, i, rs.getMetaData().
                    getColumnTypeName(i)), SQLQueryResults.
                    SQLStateOutOfRange);
        } else {
            // Returns the value read
            return value;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Byte} starting from a {@link Short}.
     *
     * @param value {@link Short} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByte(final Short value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the byte limits
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Byte.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.byteValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Byte} starting from a {@link Character}.
     *
     * @param value {@link Character} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByte(final Character value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the byte limits
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Byte.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return (byte) value.charValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Byte} starting from a {@link Integer}.
     *
     * @param value {@link Integer} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByte(final Integer value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the byte limits
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Byte.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.byteValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Byte} starting from a {@link Long}.
     *
     * @param value {@link Long} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByte(final Long value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the byte limits
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Byte.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.byteValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Byte} starting from a {@link Float}.
     *
     * @param value {@link Float} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByte(final Float value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the byte limits
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                throw new ArithmeticException(Byte.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.byteValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Byte} starting from a {@link Double}.
     *
     * @param value {@link Double} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByte(final Double value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the byte limits
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Byte.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.byteValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that converts the text
     * representation of an object into a {@link Byte}.
     *
     * @param value {@link Object} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByteFromStr(final Object value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the string into a byte
            return Byte.valueOf(value.toString());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Byte} starting from a {@link BigInteger}.
     *
     * @param value {@link BigInteger} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByte(final BigInteger value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a byte
            return value.byteValueExact();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Byte} starting from a {@link BigDecimal}.
     *
     * @param value {@link BigDecimal} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByte(final BigDecimal value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a byte
            return value.byteValueExact();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Byte} starting from a {@link Boolean}.
     *
     * @param value {@link Boolean} value to be converted.
     * @return {@link Byte} corresponding to the value provided.
     */
    private static Byte toByte(final Boolean value) {
        // Check if the value is not null
        if (value != null) {
            // Check the value provided
            if (value) {
                // Return the byte one
                return NumberUtils.BYTE_ONE;
            } else {
                // Return the byte zero
                return NumberUtils.BYTE_ZERO;
            }
        } else {
            // Return null value
            return null;
        }
    }//</editor-fold>
        
    //<editor-fold defaultstate="collapsed" desc="Internal utility methods to convert a Long.">
    /**
     * Internal utility method that returns a
     * {@link Long} starting from a {@link Byte}.
     *
     * @param value {@link Byte} value to be converted.
     * @return {@link Long} corresponding to the value provided.
     */
    private static Long toLong(final Byte value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.longValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Long} starting from a {@link Short}.
     *
     * @param value {@link Short} value to be converted.
     * @return {@link Long} corresponding to the value provided.
     */
    private static Long toLong(final Short value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.longValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Integer} starting from a {@link Short}.
     *
     * @param value {@link Short} value to be converted.
     * @return {@link Integer} corresponding to the value provided.
     */
    private static Long toLong(final Integer value) {
        // Check if the value is not null
        if (value != null) {
            // Returns the converted value
            return value.longValue();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Long} starting from a {@link Float}.
     *
     * @param value {@link Float} value to be converted.
     * @return {@link Long} corresponding to the value provided.
     */
    private static Long toLong(final Float value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the long limits
            if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Long.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.longValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Long} starting from a {@link Double}.
     *
     * @param value {@link Double} value to be converted.
     * @return {@link Long} corresponding to the value provided.
     */
    private static Long toLong(final Double value) {
        // Check if the value is not null
        if (value != null) {
            // Check if the value is outside the long limits
            if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
                // Raises an exception indicating an arithmetic overflow
                throw new ArithmeticException(Long.class.getSimpleName().
                        concat(SQLQueryResults.OverflowSuffix));
            } else {
                // Returns the converted value
                return value.longValue();
            }
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that converts the text
     * representation of an object into a {@link Long}.
     *
     * @param value {@link Object} value to be converted.
     * @return {@link Long} corresponding to the value provided.
     */
    private static Long toLongFromStr(final Object value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the string into a long
            return Long.valueOf(value.toString());
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Long} starting from a {@link BigInteger}.
     *
     * @param value {@link BigInteger} value to be converted.
     * @return {@link Long} corresponding to the value provided.
     */
    private static Long toLong(final BigInteger value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a long
            return value.longValueExact();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Long} starting from a {@link BigDecimal}.
     *
     * @param value {@link BigDecimal} value to be converted.
     * @return {@link Long} corresponding to the value provided.
     */
    private static Long toLong(final BigDecimal value) {
        // Check if the value is not null
        if (value != null) {
            // Converts the value into a long
            return value.longValueExact();
        } else {
            // Return null value
            return null;
        }
    }
    
    /**
     * Internal utility method that returns a
     * {@link Long} starting from a {@link Boolean}.
     *
     * @param value {@link Boolean} value to be converted.
     * @return {@link Long} corresponding to the value provided.
     */
    private static Long toLong(final Boolean value) {
        // Check if the value is not null
        if (value != null) {
            // Check the value provided
            if (value) {
                // Return the value one
                return NumberUtils.LONG_ONE;
            } else {
                // Return the value zero
                return NumberUtils.LONG_ZERO;
            }
        } else {
            // Return null value
            return null;
        }
    }//</editor-fold>
        
    /**
     * {@inheritDoc}
     */
    @Override
    public final Class<?> getFieldClass(final String name) {
        // Invokes the method that uses the field index
        return this.getFieldClass(this.getFieldIndex(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Class<?> getFieldClass(final int index) {
        // Returns the class of the i-th field
        return this.mFields[index].mCls;
    }

    /**
     * {@inheritDoc}
     * <h3>Implementation note:</h3>This implementation
     * performs a binary search in logarithmic time.
     */
    @Override
    public final int getFieldIndex(final String name) {
        // Declares a support reference
        SQLQueryResults.Field field;
        // Declares four support integers
        int mid, cmp, high, low = 0;
        // Get the index of the last field
        high = this.mColumnCount - 1;
        // Itera as long as the search
        // space is not exhausted
        while (low <= high) {
            // Gets the median index
            mid = (low + high) >>> 1;
            // Retrieve the reference to this field
            field = this.mSortedFields[mid];
            // Compare the name of the field
            // with the one provided as input
            cmp = field.mName.compareToIgnoreCase(name);
            // Check if the field is in the upper half
            if (cmp < 0) {
                // Discard the lower half
                // of the search space
                low = mid + 1;
            }
            // Check if the field is in the lower half
            else if (cmp > 0) {
                // Discard the upper half
                // of the search space
                high = mid - 1;
            } else {
                // Returns the index of the field
                return field.mIndex;
            }
        }
        // Indicates that the field is not found
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getFieldName(final int index) {
        // Returns the name of the i-th field
        return this.mFields[index].mName;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isSigned(final String name) {
        // Invokes the method that uses the field index
        return this.isSigned(this.getFieldIndex(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isSigned(final int index) {
        // Returns the sign flag of the i-th field
        return this.mFields[index].mSigned;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<QueryRecord> iterator() {
        // Returns the iterator over the records
        return this.mRows.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getFieldCount() {
        // Returns the number of fields
        // that make up the results
        return this.mColumnCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getRecordCount() {
        // Returns the number of records
        // that make up the results
        return this.mRows.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasRecords() {
        // Indicates if there is at least one record
        return this.mRows.size() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        // Declares a support integer
        final int len;
        // Declares a support reference
        final StringBuilder buffer;
        // Declares a support reference
        final Iterator<QueryRecord> it;
        // Instantiates a text buffer
        buffer = new StringBuilder();
        // Get the iterator on the records
        it = this.mRows.iterator();
        // Writes the array of fields that make up the records
        buffer.append(Arrays.toString(this.mFields));
        // Calculate the length of the separation line
        len = Math.max(buffer.length(), 25);
        // Append the string that separates the two lines
        buffer.append(System.lineSeparator());
        // Add a line to separate the header line
        buffer.append(StringUtils.repeat('~', len));
        // Check that there is at least one record
        if (it.hasNext()) {
            // Starts do-while cycle
            do {
                // Append the string that separates the two lines
                buffer.append(System.lineSeparator());
                // Append the textual representation of the record
                buffer.append(it.next().toString());
            }
            // Itera over the other records
            while (it.hasNext());
        } else {
            // Append the string that separates the two lines
            buffer.append(System.lineSeparator());
            // Adds a label to indicate that there are no records
            buffer.append(SQLQueryResults.NoResultsLabel);
        }
        // Return the contents of the buffer
        return buffer.toString();
    }
}
