
package com.coolplanet.task.adapter;

import com.coolplanet.task.application.Handler;
import com.coolplanet.task.application.ProcessorHandler;
import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.domain.model.TaskResponse;
import com.coolplanet.task.domain.model.WorkflowType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * TaskController is a Spring REST controller responsible for handling task-related operations.
 * It exposes endpoints for recording task processing information and calculating task-specific metrics,
 * such as the average duration.
 *
 * Annotations:
 * - {@code @Slf4j}: Enables logging within the class.
 * - {@code @RestController}: Marks this class as a REST controller for handling HTTP requests.
 * - {@code @RequestMapping("/tasks")}: Maps the base path for all endpoints to "/tasks".
 *
 * Constructor:
 * - TaskController(ProcessorHandler handler): Initializes the TaskController with a provided {@code ProcessorHandler}.
 *
 * Endpoints:
 * - {@code recordTask(String id, Long duration)}:
 *   Handles HTTP POST requests to record task details for processing.
 *   - Path: {@code /tasks}
 *   - Request Parameters:
 *     - {@code id} (String): The unique task identifier, must be non-empty and valid.
 *     - {@code duration} (Long): The task duration in milliseconds, must be positive.
 *   - Returns: A reactive {@code Mono} of {@code ResponseEntity<String>} containing the processing status.
 *   - Logs information about the task being processed.
 *   - Creates a {@code TaskContext} for the task processing workflow and delegates handling to the {@code handler}.
 *
 * - {@code taskAverage(String id)}:
 *   Handles HTTP GET requests to calculate the average duration for a specified task.
 *   - Path: {@code /tasks/{id}/average}
 *   - Path Variables:
 *     - {@code id} (String): The unique task identifier, must be non-null and valid.
 *   - Returns: A reactive {@code Mono} of {@code ResponseEntity<TaskDTO>} containing the task average duration details.
 *   - Logs information about the task for which the average is being calculated.
 *   - Creates a {@code TaskContext} for the average duration calculation workflow and delegates handling to the {@code handler}.
 *
 * Private Utility Method:
 * - {@code taskContext(String id, Long duration, WorkflowType workflowType)}:
 *   Constructs a {@code TaskContext} object with the specified parameters:
 *   - {@code id} (String): The task identifier.
 *   - {@code duration} (Long): The task duration.
 *   - {@code workflowType} (WorkflowType): The type of workflow to process.
 *   Returns a new {@code TaskContext}.
 *
 * Responsibilities:
 * - Acts as a mediator between client HTTP requests and task processing workflows.
 * - Constructs appropriate {@code TaskContext} objects based on client input to define workflows.
 * - Delegates processing logic to the {@code ProcessorHandler}.
 *
 * Error Handling:
 * - Relies on validation annotations {@code @Valid}, {@code @NotEmpty}, {@code @Positive}, and {@code @NotNull}
 *   to ensure that endpoint inputs are valid.
 *
 * Reactive Behavior:
 * - Uses Project Reactor's {@code Mono} to handle asynchronous and non-blocking operations efficiently.
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@Tag(name = "Task Operations", description = "APIs for managing and analyzing tasks")
class TaskController {

    private final Handler handler;

    TaskController(ProcessorHandler handler) {
        this.handler = handler;
    }

    @Operation(
            summary = "Record a new task",
            description = "Records a new task with the specified ID and duration. " +
                    "The task is processed asynchronously and the status is returned immediately."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Task accepted for processing",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Error.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<ResponseEntity<TaskResponse>> recordTask(
            @Parameter(
                    description = "Unique identifier for the task",
                    required = true,
                    example = "task-123"
            )
            @Valid @NotBlank @RequestParam String taskId,

            @Parameter(
                    description = "Duration of the task in milliseconds",
                    required = true,
                    example = "1000"
            )
            @Valid @Positive @RequestParam Long duration) {

        log.info("Processing task: {}", taskId);

        TaskContext taskContext = taskContext(taskId, duration, WorkflowType.PROCESS_TASK);
        return handler.handle(taskContext)
                .map(obj -> ResponseEntity.status(HttpStatus.CREATED).body((TaskResponse) obj));
    }

    @Operation(
            summary = "Get task average duration",
            description = "Retrieves the average duration for a specific task based on its ID. " +
                    "Calculates the average of all recorded durations for the specified task."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Average duration calculation accepted",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid task ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Error.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Error.class)
                    )
            )
    })
    @GetMapping("/{taskId}/average")
    public Mono<ResponseEntity<TaskDTO>> taskAverage(
            @Parameter(
                    description = "ID of the task to calculate average for",
                    required = true,
                    example = "task-123"
            )
            @Valid @NotBlank @PathVariable String taskId) {

        log.info("Calculate average duration for task: {}", taskId);

        TaskContext taskContext = taskContext(taskId, 0L, WorkflowType.CALCULATE_AVERAGE_DURATION);
        return handler.handle(taskContext)
                .map(taskDto -> ResponseEntity.ok().body((TaskDTO) taskDto));
    }

    private static TaskContext taskContext(String id, Long duration, WorkflowType workflowType) {
        return TaskContext.builder()
                .request(new TaskDTO(id, duration))
                .workflowType(workflowType)
                .build();
    }
}
