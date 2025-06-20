package com.coolplanet.task.application.exception;

import com.coolplanet.task.domain.exception.ProcessingException;
import com.coolplanet.task.domain.exception.TaskNotFoundException;
import com.coolplanet.task.domain.model.TrackerErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for {@code GlobalExceptionHandler}.
 *
 * This test class verifies the behavior of the {@code GlobalExceptionHandler} class
 * which is responsible for handling application-specific exceptions and
 * returning structured error responses in the form of {@code TrackerErrorResponse}.
 *
 * It contains test cases for the following exception handling scenarios:
 *
 * 1. {@code ProcessingException} handling, including verification of the response's
 *    message, error code, status code, and timestamp.
 * 2. {@code RecordNotFoundException} handling, including verification of the response's
 *    message, error code, status code, and timestamp.
 * 3. Validation of the HTTP status codes returned for specific exceptions.
 * 4. Validating the timestamp format in the generated error response against the ISO-8601 format.
 *
 * The tests ensure that the exception handling mechanism conforms to
 * expected application requirements and produces responses with all necessary fields populated correctly.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void processingException_ShouldReturnTrackerErrorResponse() {
        // Arrange
        String errorMessage = "Processing failed";
        ProcessingException exception = new ProcessingException(errorMessage);

        // Act
        TrackerErrorResponse response = globalExceptionHandler.processingException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(errorMessage, response.message());
        assertEquals(exception.getErrorCode(), response.errorCode());
        assertEquals(exception.getStatusCode(), response.statusCode());
        assertNotNull(response.timestamp());
    }

    @Test
    void recordNotFoundException_ShouldReturnTrackerErrorResponse() {
        // Arrange
        String errorMessage = "Record not found";
        TaskNotFoundException exception = new TaskNotFoundException(errorMessage);

        // Act
        TrackerErrorResponse response = globalExceptionHandler.recordNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(errorMessage, response.message());
        assertEquals(exception.getErrorCode(), response.errorCode());
        assertEquals(exception.getStatusCode(), response.statusCode());
        assertNotNull(response.timestamp());
    }

    @Test
    void processingException_ShouldReturnCorrectHttpStatus() {
        // Arrange
        ProcessingException exception = new ProcessingException("Error");

        // Act
        TrackerErrorResponse response = globalExceptionHandler.processingException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.statusCode());
    }

    @Test
    void recordNotFoundException_ShouldReturnCorrectHttpStatus() {
        // Arrange
        TaskNotFoundException exception = new TaskNotFoundException("Not found");

        // Act
        TrackerErrorResponse response = globalExceptionHandler.recordNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
    }

    @Test
    void timestamp_ShouldBeInISO8601Format() {
        // Arrange
        ProcessingException exception = new ProcessingException("Error");

        // Act
        TrackerErrorResponse response = globalExceptionHandler.processingException(exception);

        // Assert
        assertNotNull(response.timestamp());
        assertTrue(response.timestamp().matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?"));
    }
}