package com.coolplanet.task.domain.exception;

/**
 * An enumeration representing application-defined error codes for
 * categorizing and identifying exceptions.
 *
 * This enum is typically used to provide a standardized way to represent
 * specific error scenarios throughout the application, enabling consistent
 * handling and reporting of errors. The associated error codes can be
 * retrieved programmatically and utilized in exception classes or as part
 * of error handling logic.
 *
 * Enum Constants:
 * - PROCESSING_ERROR: Represents an error during a processing operation,
 *   with the associated error code 1001.
 * - RECORD_NOT_FOUND_ERROR: Represents a scenario where a requested record
 *   or resource could not be found, with the associated error code 1002.
 *
 * Each enum constant has an internally defined integer code that can be
 * retrieved using the getCode method.
 */
public enum ErrorCodes {

    PROCESSING_ERROR(1001),
    RECORD_NOT_FOUND_ERROR(1002);

    private final int code;
    ErrorCodes(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
