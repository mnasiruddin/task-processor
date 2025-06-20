package com.coolplanet.task.application.exception;

import com.coolplanet.task.domain.exception.ProcessingException;
import com.coolplanet.task.domain.exception.TaskNotFoundException;
import com.coolplanet.task.domain.model.TrackerErrorResponse;
import com.coolplanet.task.domain.exception.TrackerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * GlobalExceptionHandler is a centralized exception handler for the application.
 * It uses Spring's {@link RestControllerAdvice} to handle exceptions and return custom error responses.
 * This class maps specific exceptions to HTTP status codes and provides a standard response format.
 *
 * It includes methods to handle specific exceptions like:
 * - {@link ProcessingException}: Mapped to HTTP 500 (Internal Server Error).
 * - {@link TaskNotFoundException}: Mapped to HTTP 404 (Not Found).
 *
 * Each exception is processed to return a {@link TrackerErrorResponse} object with details such as
 * the error message, error code, timestamp, and HTTP status code.
 *
 * The {@code trackerException} method is a utility method that transforms a {@link TrackerException}
 * into a {@link TrackerErrorResponse} object.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link ProcessingException} by mapping it to an HTTP 500 (Internal Server Error) response
     * and returning a {@link TrackerErrorResponse} containing error details.
     *
     * @param processingException the ProcessingException that occurred during request processing
     * @return a {@link TrackerErrorResponse} containing the error message, error code, timestamp, and HTTP status
     */
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ProcessingException.class)
    public TrackerErrorResponse processingException(ProcessingException processingException) {
        return trackerException(processingException);
    }

    /**
     * Handles {@link TaskNotFoundException} by mapping it to an HTTP 404 (Not Found) response
     * and returning a {@link TrackerErrorResponse} containing error details.
     *
     * @param taskNotFoundException the instance of {@link TaskNotFoundException} that occurred when a requested record was not found
     * @return a {@link TrackerErrorResponse} containing the error message, error code, timestamp, and HTTP status
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(TaskNotFoundException.class)
    public TrackerErrorResponse recordNotFoundException(TaskNotFoundException taskNotFoundException) {
        return trackerException(taskNotFoundException);
    }

    /**
     * Transforms a {@link TrackerException} into a {@link TrackerErrorResponse} object
     * containing the exception details such as the message, error code, timestamp, and status code.
     *
     * @param trackerException the TrackerException that contains the error details
     * @return a {@link TrackerErrorResponse} representing the structured error response
     */
    private TrackerErrorResponse trackerException(TrackerException trackerException) {
        return new TrackerErrorResponse(trackerException.getMessage(), trackerException.getErrorCode(), LocalDateTime.now().toString(), trackerException.getStatusCode());
    }
}
