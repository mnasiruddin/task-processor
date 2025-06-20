package com.coolplanet.task.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a response for a task processing operation.
 *
 * This record encapsulates the status of a task after it has been processed.
 * It serves as a minimal response object to convey the outcome of task-related
 * workflows or services.
 *
 * Use Cases:
 * - TaskResponse instances are typically returned by task service implementations,
 *   such as processing or workflow handling services.
 * - It is used to indicate the successful processing or to carry basic status information
 *   about the task.
 *
 * Characteristics:
 * - The field {@code status} holds a textual representation of the task's processing status
 *   (e.g., "OK" for successful processing).
 *
 * Immutability:
 * - TaskResponse is immutable, ensuring thread safety and consistency across different
 *   components when shared or reused.
 */
@Schema(description = "Task Response")
public record TaskResponse(
        @Schema(description = "Status of the task processing", example = "OK")
        String status) {
}
