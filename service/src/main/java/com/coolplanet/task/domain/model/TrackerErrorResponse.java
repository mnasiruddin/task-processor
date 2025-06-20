package com.coolplanet.task.domain.model;

import com.coolplanet.task.domain.exception.ErrorCodes;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a standardized error response structure used across the application to
 * provide detailed error information when exceptions occur.
 *
 * This record encapsulates details such as the error message, a unique error code,
 * the timestamp of the error occurrence, and the associated HTTP status code.
 * It is primarily used in exception handling to return consistent and structured error
 * responses to the client.
 *
 * Characteristics:
 * - message: A detailed error message describing the nature of the error.
 * - errorCode: A unique code identifying the specific type of error.
 * - timestamp: The date and time when the error occurred, formatted as a string.
 * - statusCode: The HTTP status code that corresponds to the error.
 *
 * Use Cases:
 * - TrackerErrorResponse instances are typically returned by global exception handler
 *   methods (e.g., in {@code GlobalExceptionHandler}) to convey error details to clients.
 * - Provides a unified format for error reporting, simplifying the debugging process
 *   and improving client-side exception handling.
 *
 * Immutability:
 * - This record is immutable, ensuring that error responses cannot be modified once created,
 *   which helps maintain consistency and thread safety.
 */
@Schema(description = "Error Response")
public record TrackerErrorResponse(
        @Schema(description = "Error message", example = "Invalid input parameters")
        String message,
        @Schema(description = "Error code", example = "ErrorCodes.PROCESSING_ERROR")
        int errorCode,
        @Schema(description = "Error code", example = "2025-06-20T12:58:47.489985")
        String timestamp,
        @Schema(description = "Error code", example = "400")
        int statusCode) {
}
