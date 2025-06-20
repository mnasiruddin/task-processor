package com.coolplanet.task.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a Data Transfer Object (DTO) for a task.
 *
 * This record is designed to encapsulate basic information about a task,
 * including its unique identifier and the total duration associated with it.
 *
 * Use Cases:
 * - TaskDTO instances are primarily used to transfer task-related data
 *   across different layers or components of the application.
 * - They are often included as part of requests or responses to facilitate
 *   task processing workflows, such as calculating average task durations
 *   or persisting task updates.
 *
 * Immutability:
 * - TaskDTO is immutable by design, ensuring thread safety and consistency
 *   when shared across components.
 *
 * Fields:
 * - taskId: A unique identifier for the task.
 * - totalDurationMs: The total processing duration of the task in milliseconds.
 */
@Schema(description = "Task Data Transfer Object")
public record TaskDTO (
        @Schema(description = "Unique identifier of the task", example = "task-123")
        String taskId,
        @Schema(description = "Total duration in milliseconds", example = "1000")
        long totalDurationMs) {
}