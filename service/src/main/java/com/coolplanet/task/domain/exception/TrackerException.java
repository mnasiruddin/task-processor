package com.coolplanet.task.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TrackerException is a custom runtime exception class used to represent
 * application-specific errors with detailed context information such as
 * error message, error code, and HTTP status code.
 *
 * This class serves as a base class for other specialized exceptions and
 * provides constructors for initializing exception details. It also includes
 * a utility method to retrieve a formatted detail message for easier debugging
 * and error tracking.
 *
 * Constructors:
 * - TrackerException(String message, int errorCode, int statusCode): Creates
 *   an instance with a custom error message, error code, and HTTP status code.
 * - TrackerException(String message, int statusCode): Creates an instance with
 *   a default error code of 1001, along with a custom error message and HTTP status code.
 *
 * Methods:
 * - fullDetailMessage(): Returns a formatted string containing the exception's
 *   message, error code, and status code for detailed reporting.
 *
 * Use Cases:
 * - Extend this class to define more specific exceptions that capture additional
 *   or specialized error scenarios, such as RecordNotFoundException or
 *   ProcessingException.
 * - Utilize this exception to encapsulate meaningful error context for consistent
 *   handling across the application.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TrackerException extends RuntimeException {
    private final String message;
    private final int errorCode;
    private final int statusCode;

    public TrackerException(String message, int errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public TrackerException(String message, int statusCode) {
        this(message, 1001, statusCode);
    }

    public String fullDetailMessage() {
        return String.format("%s : with Error Code (%s) and StatusCode (%d)", message, errorCode, statusCode);
    }

}
