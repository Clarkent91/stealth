/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.util;

import it.stealth.sql.readers.SQLQueryReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Provides some useful tools for
 * interacting with the JDBC APIs.
 * 
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public final class SQLUtils
{
    /**
     * Exception message raised when the JDBC driver is not found.
     */
    private static final String ExJdbcDriverNotFound = "JDBC driver %s not found";
    
    /**
     * Defines the date format used to write the time.
     */
    public static final DateFormat SQLTimeFormat = new SimpleDateFormat("HH:mm:ss");
    
    /**
     * Defines the date format used to write the date.
     */
    public static final DateFormat SQLDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Defines the date format used to write the date and time.
     */
    public static final DateFormat SQLDatetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Defines the {@link DateTimeFormatter} used to write the time.
     */
    public static final DateTimeFormatter SQLTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * Defines the {@link DateTimeFormatter} used to write the date.
     */
    public static final DateTimeFormatter SQLDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Defines the {@link DateTimeFormatter} used to write the date and time.
     */
    public static final DateTimeFormatter SQLDatetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Defines a simple implementation of the {@link SQLQueryReader}
     * interface to determine if an SQL query provides at least one result.
     */
    public static final SQLQueryReader<Boolean> HasQueryResults = (final ResultSet rs) -> {
        // Check if there is at least one record
        return rs.next();
    };
    
    /**
     * Closes a JDBC statement by invoking the {@link Statement#close()} method.
     * <p>It is <b>strongly recommended</b> that an application explicitly commits
     * or rolls back an active transaction prior to calling the {@code close} method.
     * If the {@code close} method is called and there is an active transaction, the
     * results are implementation-defined.</p>
     * 
     * @param dbStatement SQL statement connected to the database to be closed.
     * @throws SQLException if an error occurs during the closing process.
     */
    public static final void close(final Statement dbStatement) throws SQLException {
        // Try to close the statement if it is not null
        if (dbStatement != null) dbStatement.close();
    }
    
    /**
     * Closes a JDBC connection by invoking the {@link Connection#close()} method.
     * <p>It is <b>strongly recommended</b> that an application explicitly commits
     * or rolls back an active transaction prior to calling the {@code close} method.
     * If the {@code close} method is called and there is an active transaction, the
     * results are implementation-defined.</p>
     *
     * @param dbConn connection to the database to be closed.
     * @throws SQLException if an error occurs during the closing process.
     */
    public static final void close(final Connection dbConn) throws SQLException {
        // Try to close the connection if it is not null
        if (dbConn != null) dbConn.close();
    }
    
    /**
     * Retrieves the value of the designated column in the current row of this
     * {@link java.sql.ResultSet} object as a {@link java.time.LocalDateTime}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.time.LocalDateTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final LocalDateTime readLocalDateTime(final
            ResultSet rs, final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Timestamp timestamp = rs.getTimestamp(columnIndex);
        // If the column is null, it returns null, otherwise
        // it converts the value to java.time.LocalDateTime
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of this
     * {@link java.sql.ResultSet} object as a {@link java.time.LocalDateTime}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.time.LocalDateTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final LocalDateTime readLocalDateTime(final
            ResultSet rs, final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Timestamp timestamp = rs.getTimestamp(columnName);
        // If the column is null, it returns null, otherwise
        // it converts the value to java.time.LocalDateTime
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of this
     * {@link java.sql.ResultSet} object as a {@link java.time.OffsetTime} in
     * the Java programming language (based on the {@link java.time.ZoneId}
     * provided).
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @param zoneId the time-zone, which may be an offset, not {@code null}.
     * @return {@link java.time.OffsetTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final OffsetTime readOffsetTime(final ResultSet rs, final
            int columnIndex, final ZoneId zoneId) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Time value = rs.getTime(columnIndex);
        // If the column is null, it returns null, otherwise it converts
        // the value to OffsetTime based on the ZoneId provided
        return value != null ? OffsetTime.ofInstant(
                value.toInstant(), zoneId) : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of this
     * {@link java.sql.ResultSet} object as a {@link java.time.OffsetTime} in
     * the Java programming language (based on the {@link java.time.ZoneId}
     * provided).
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @param zoneId the time-zone, which may be an offset, not {@code null}.
     * @return {@link java.time.OffsetTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final OffsetTime readOffsetTime(final ResultSet rs, final
            String columnName, final ZoneId zoneId) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Timestamp timestamp = rs.getTimestamp(columnName);
        // If the column is null, it returns null, otherwise it converts
        // the value to OffsetTime based on the ZoneId provided
        return timestamp != null ? OffsetTime.ofInstant(
                timestamp.toInstant(), zoneId) : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of this
     * {@link java.sql.ResultSet} object as a {@link java.time.OffsetDateTime}
     * in the Java programming language (based on the {@link java.time.ZoneId}
     * provided).
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @param zoneId the time-zone, which may be an offset, not {@code null}.
     * @return {@link java.time.OffsetDateTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final OffsetDateTime readOffsetDateTime(final ResultSet rs,
            final int columnIndex, final ZoneId zoneId) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Timestamp timestamp = rs.getTimestamp(columnIndex);
        // If the column is null, it returns null, otherwise it converts
        // the value to OffsetDateTime based on the ZoneId provided
        return timestamp != null ? OffsetDateTime.ofInstant(
                timestamp.toInstant(), zoneId) : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of this
     * {@link java.sql.ResultSet} object as a {@link java.time.OffsetDateTime}
     * in the Java programming language (based on the {@link java.time.ZoneId}
     * provided).
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @param zoneId the time-zone, which may be an offset, not {@code null}.
     * @return {@link java.time.OffsetDateTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final OffsetDateTime readOffsetDateTime(final ResultSet rs,
            final String columnName, final ZoneId zoneId) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Timestamp timestamp = rs.getTimestamp(columnName);
        // If the column is null, it returns null, otherwise it converts
        // the value to OffsetDateTime based on the ZoneId provided
        return timestamp != null ? OffsetDateTime.ofInstant(
                timestamp.toInstant(), zoneId) : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of this
     * {@link java.sql.ResultSet} object as a {@link java.time.ZonedDateTime}
     * in the Java programming language (based on the {@link java.time.ZoneId}
     * provided).
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @param zoneId the time-zone, which may be an offset, not {@code null}.
     * @return {@link java.time.ZonedDateTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final ZonedDateTime readZonedDateTime(final ResultSet rs,
            final int columnIndex, final ZoneId zoneId) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Timestamp timestamp = rs.getTimestamp(columnIndex);
        // If the column is null, it returns null, otherwise it converts
        // the value to ZonedDateTime based on the ZoneId provided
        return timestamp != null ? ZonedDateTime.ofInstant(
                timestamp.toInstant(), zoneId) : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of this
     * {@link java.sql.ResultSet} object as a {@link java.time.ZonedDateTime}
     * in the Java programming language (based on the {@link java.time.ZoneId}
     * provided).
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @param zoneId the time-zone, which may be an offset, not {@code null}.
     * @return {@link java.time.ZonedDateTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final ZonedDateTime readZonedDateTime(final ResultSet rs,
            final String columnName, final ZoneId zoneId) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Timestamp timestamp = rs.getTimestamp(columnName);
        // If the column is null, it returns null, otherwise it converts
        // the value to ZonedDateTime based on the ZoneId provided
        return timestamp != null ? ZonedDateTime.ofInstant(
                timestamp.toInstant(), zoneId) : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.time.LocalDate}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.time.LocalDate} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final LocalDate readLocalDate(final ResultSet
            rs, final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Date date = rs.getDate(columnIndex);
        // If the column is null, it returns null, otherwise
        // it converts the value to java.time.LocalDate
        return date != null ? date.toLocalDate() : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.time.LocalDate}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.time.LocalDate} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final LocalDate readLocalDate(final ResultSet
            rs, final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Date date = rs.getDate(columnName);
        // If the column is null, it returns null, otherwise
        // it converts the value to java.time.LocalDate
        return date != null ? date.toLocalDate() : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.time.LocalTime}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.time.LocalTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final LocalTime readLocalTime(final ResultSet
            rs, final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Time value = rs.getTime(columnIndex);
        // If the column is null, it returns null, otherwise
        // it converts the value to java.time.LocalTime
        return value != null ? value.toLocalTime() : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.time.LocalTime}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.time.LocalTime} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final LocalTime readLocalTime(final ResultSet
            rs, final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Time value = rs.getTime(columnName);
        // If the column is null, it returns null, otherwise
        // it converts the value to java.time.LocalTime
        return value != null ? value.toLocalTime() : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.time.Instant}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.time.Instant} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Instant readInstant(final ResultSet
            rs, final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Timestamp value = rs.getTimestamp(columnName);
        // If the column is null, it returns null, otherwise
        // it converts the value to java.time.Instant
        return value != null ? value.toInstant() : null;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.time.Instant}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.time.Instant} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Instant readInstant(final ResultSet
            rs, final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final Timestamp value = rs.getTimestamp(columnIndex);
        // If the column is null, it returns null, otherwise
        // it converts the value to java.time.Instant
        return value != null ? value.toInstant() : null;
    }
    
    /**
     * Utility method that allows the loading of a JDBC driver.
     * <p>This method is called internally by the framework
     * and should not be called outside of it.</p>
     * 
     * @param jdbcDriverClasspath classpath of the JDBC driver to load.
     * @throws SQLException if the JDBC driver is not found.
     */
    public static final void loadJdbcDriver(final String
            jdbcDriverClasspath) throws SQLException {
        // Starts a try-catch block
        try {
            // Load the JDBC driver to connect to the database
            Class.forName(jdbcDriverClasspath);
        }
        // Capture any exception due to failure to load the driver
        catch (final ClassNotFoundException ex) {
            // Wrap the current exception indicating
            // that the JDBC driver was not found
            throw new SQLException(String.format(SQLUtils.
                    ExJdbcDriverNotFound, jdbcDriverClasspath), ex);
        }
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Byte}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.lang.Byte} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Byte readByte(final ResultSet rs,
            final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final byte value = rs.getByte(columnIndex);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Byte}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.lang.Byte} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Byte readByte(final ResultSet rs,
            final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final byte value = rs.getByte(columnName);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Integer}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.lang.Integer} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Integer readInteger(final ResultSet rs,
            final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final int value = rs.getInt(columnIndex);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Integer}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.lang.Integer} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Integer readInteger(final ResultSet rs,
            final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final int value = rs.getInt(columnName);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Long}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.lang.Long} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Long readLong(final ResultSet rs,
            final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final long value = rs.getLong(columnIndex);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Long}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.lang.Long} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Long readLong(final ResultSet rs,
            final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final long value = rs.getLong(columnName);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Short}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.lang.Short} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Short readShort(final ResultSet rs,
            final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final short value = rs.getShort(columnIndex);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Short}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.lang.Short} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Short readShort(final ResultSet rs,
            final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final short value = rs.getShort(columnName);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Float}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.lang.Float} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Float readFloat(final ResultSet rs,
            final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final float value = rs.getFloat(columnIndex);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Float}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.lang.Float} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Float readFloat(final ResultSet rs,
            final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final float value = rs.getFloat(columnName);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Double}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.lang.Double} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Double readDouble(final ResultSet rs,
            final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final double value = rs.getDouble(columnIndex);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Double}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.lang.Double} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Double readDouble(final ResultSet rs,
            final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final double value = rs.getDouble(columnName);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Boolean}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnIndex column index in which to read the value.
     * @return {@link java.lang.Boolean} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Boolean readBoolean(final ResultSet rs,
            final int columnIndex) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final boolean value = rs.getBoolean(columnIndex);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of
     * this {@link java.sql.ResultSet} object as a {@link java.lang.Boolean}
     * in the Java programming language.
     * 
     * @param rs {@link java.sql.ResultSet} from which to read the requested value.
     * @param columnName column name in which to read the value.
     * @return {@link java.lang.Boolean} value contained in the i-th column.
     * @throws SQLException if the columnIndex is not valid; if a database
     * access error occurs or this method is called on a closed result set.
     */
    public static final Boolean readBoolean(final ResultSet rs,
            final String columnName) throws SQLException {
        // Retrieves the value of the designated column in the current row
        final boolean value = rs.getBoolean(columnName);
        // If the column is null, it returns null,
        // otherwise it wraps the primitive value
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Writes a {@link LocalDateTime} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeLocalDateTime(final PreparedStatement stmt, final
            int columnIndex, final LocalDateTime value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Converts the value and writes it to the SQL statement
            stmt.setTimestamp(columnIndex, SQLUtils.toSQLTimestampFrom(value));
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.TIMESTAMP);
        }
    }
    
    /**
     * Writes a {@link LocalTime} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeLocalTime(final PreparedStatement stmt,
            final int columnIndex, final LocalTime value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Converts the value and writes it to the SQL statement
            stmt.setTime(columnIndex, SQLUtils.toSQLTimeFrom(value));
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.TIME);
        }
    }
    
    /**
     * Writes a {@link LocalDate} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeLocalDate(final PreparedStatement stmt,
            final int columnIndex, final LocalDate value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Converts the value and writes it to the SQL statement
            stmt.setDate(columnIndex, SQLUtils.toSQLDateFrom(value));
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.DATE);
        }
    }
    
    /**
     * Writes a {@link Boolean} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeBoolean(final PreparedStatement stmt, final
            int columnIndex, final Boolean value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Write the value inside the SQL statement
            stmt.setBoolean(columnIndex, value);
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.BOOLEAN);
        }
    }
    
    /**
     * Writes a {@link Byte} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeByte(final PreparedStatement stmt, final
            int columnIndex, final Byte value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Write the value inside the SQL statement
            stmt.setByte(columnIndex, value);
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.TINYINT);
        }
    }
    
    /**
     * Writes a {@link Short} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeShort(final PreparedStatement stmt, final
            int columnIndex, final Short value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Write the value inside the SQL statement
            stmt.setShort(columnIndex, value);
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.SMALLINT);
        }
    }
    
    /**
     * Writes a {@link Integer} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeInt(final PreparedStatement stmt, final
            int columnIndex, final Integer value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Write the value inside the SQL statement
            stmt.setInt(columnIndex, value);
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.INTEGER);
        }
    }
    
    /**
     * Writes a {@link Long} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeLong(final PreparedStatement stmt, final
            int columnIndex, final Long value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Write the value inside the SQL statement
            stmt.setLong(columnIndex, value);
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.BIGINT);
        }
    }
    
    /**
     * Writes a {@link Float} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeFloat(final PreparedStatement stmt, final
            int columnIndex, final Float value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Write the value inside the SQL statement
            stmt.setFloat(columnIndex, value);
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.FLOAT);
        }
    }
    
    /**
     * Writes a {@link Double} parameter within a {@link PreparedStatement}.
     * <p>Unlike the method provided by the {@link PreparedStatement} interface,
     * this method allows the management of the {@code null} value.</p>
     * 
     * @param stmt the {@link PreparedStatement} on which to write the value.
     * @param columnIndex column index in which to write the value.
     * @param value value that must be written in the {@link PreparedStatement}.
     * @throws SQLException if {@code columnIndex} does not correspond to a parameter
     *         marker in the SQL statement; if a database access error occurs or
     *         this method is called on a closed {@link PreparedStatement}.
     */
    public static final void writeDouble(final PreparedStatement stmt, final
            int columnIndex, final Double value) throws SQLException {
        // Check if the value is different from null
        if (value != null) {
            // Write the value inside the SQL statement
            stmt.setDouble(columnIndex, value);
        } else {
            // Write NULL value inside the SQL statement
            stmt.setNull(columnIndex, Types.DOUBLE);
        }
    }
    
    /**
     * Internal procedure that covers an {@link java.time.LocalDateTime}
     * object in a {@link java.sql.Timestamp} object.
     * 
     * @param instant value to be converted.
     * @return corresponding {@link java.sql.Time} object.
     */
    private static Timestamp toSQLTimestampFrom(final LocalDateTime datetime) {
        // Starts a try-catch block
        try {
            // Creates a new Timestamp object based on the input provided
            return new Timestamp(TimeUnit.SECONDS.toMillis(
                    datetime.toEpochSecond(ZoneOffset.UTC)));
        }
        // Capture every arithmetic overflow error
        catch (final ArithmeticException ex) {
            // Wraps this error in an IllegalArgumentException
            throw new IllegalArgumentException(ex);
        }
    }
    
    /**
     * Internal procedure that covers an {@link java.time.LocalTime}
     * object in a {@link java.sql.Time} object.
     * 
     * @param instant value to be converted.
     * @return corresponding {@link java.sql.Time} object.
     */
    private static Time toSQLTimeFrom(final LocalTime time) {
        // Starts a try-catch block
        try {
            // Creates a new Time object
            // based on the input provided
            return new Time(TimeUnit.NANOSECONDS.
                    toMillis(time.toNanoOfDay()));
        }
        // Capture every arithmetic overflow error
        catch (final ArithmeticException ex) {
            // Wraps this error in an IllegalArgumentException
            throw new IllegalArgumentException(ex);
        }
    }
    
    /**
     * Internal procedure that covers an {@link java.time.LocalDate}
     * object in a {@link java.sql.Time} object.
     * 
     * @param instant value to be converted.
     * @return corresponding {@link java.sql.Time} object.
     */
    private static Date toSQLDateFrom(final LocalDate date) {
        // Starts a try-catch block
        try {
            // Creates a new Date object
            // based on the input provided
            return new Date(TimeUnit.DAYS.toMillis(
                    date.toEpochDay()));
        }
        // Capture every arithmetic overflow error
        catch (final ArithmeticException ex) {
            // Wraps this error in an IllegalArgumentException
            throw new IllegalArgumentException(ex);
        }
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private SQLUtils() {}
}
