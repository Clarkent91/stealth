/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stealth.sql.util;

import java.sql.SQLException;

/**
 * Defines the exception raised when a database access violation occurs.
 * <p>This happens all the times when you do not have access privileges
 * to the database or when access is denied for other reasons.</p>
 *
 * @author Fabrizio Lo Verde
 * @version 1.0
 */
public class SQLAccessViolationException extends SQLException
{
    /**
     * Constructs a {@link SQLAccessViolationException}
     * object with a given {@code cause}.
     * 
     * @param cause the underlying reason for this exception.
     * @throws NullPointerException if {@code cause} is {@code null}.
     */
    public SQLAccessViolationException(final SQLException cause) {
        // Invokes the overloaded constructor
        this(cause.getMessage(), cause);
    }
    
    /**
     * Constructs a {@link SQLAccessViolationException}
     * object with a given {@code reason} and {@code cause}.
     * 
     * @param reason a description of the exception.
     * @param cause the underlying reason for this exception.
     * @throws NullPointerException if {@code cause} is {@code null}.
     */
    public SQLAccessViolationException(final String reason, final SQLException cause) {
        // Invokes the superclass constructor
        super(reason, cause.getSQLState(), cause.getErrorCode(), cause);
    }
}
