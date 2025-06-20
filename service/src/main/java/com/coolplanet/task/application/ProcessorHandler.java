package com.coolplanet.task.application;

import com.coolplanet.task.application.service.TaskAverageService;
import com.coolplanet.task.application.service.TaskProcessorService;
import com.coolplanet.task.application.service.TaskService;
import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.domain.model.TaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * ProcessorHandler is a Spring-managed component that serves as a handler for processing tasks
 * based on the workflow type defined in the provided TaskContext. It implements the `Handler`
 * interface and delegates the processing of tasks to specific TaskService implementations.
 *
 * Responsibilities:
 * - Handles task processing by determining the appropriate workflow type from TaskContext.
 * - Delegates processing either to a task-specific processing service or to an average duration calculation service.
 * - Logs information about the workflow type being processed.
 * - Throws an IllegalArgumentException in case of unsupported or unknown workflow types.
 *
 * Constructor:
 * - Accepts two task services:
 *   - TaskProcessorService: Handles the processing of tasks.
 *   - TaskAverageService: Calculates the average duration of tasks.
 *
 * Methods:
 * - handle(TaskContext context):
 *   - Determines the workflow type from the context and invokes the corresponding service for processing.
 *   - Workflow types supported:
 *     - PROCESS_TASK: Delegates processing to the taskProcessorService.
 *     - CALCULATE_AVERAGE_DURATION: Delegates processing to the taskAverageService.
 *   - Returns a reactive Mono wrapping the result of the processing.
 *   - Throws an IllegalArgumentException for unknown workflow types.
 *
 * Annotations:
 * - @Slf4j: Enables logging capabilities.
 * - @Component: Marks this class as a Spring-managed component.
 *
 * Reactive Behavior:
 * - The handle method returns a Mono, ensuring that the task processing is non-blocking and adheres to
 *   reactive principles.
 *
 * Use Cases:
 * - This class is used to route and handle task processing based on the type of workflow provided in the task context.
 * - It enables dynamic handling of different workflows in a reactive, decoupled manner.
 *
 * Error Handling:
 * - Throws an IllegalArgumentException for unsupported or unknown workflow types to ensure proper validation.
 */
@Slf4j
@Component
public class ProcessorHandler implements Handler {

    private final TaskService<TaskResponse, TaskContext> taskProcessorService;
    private final TaskService<TaskDTO, TaskContext> taskAverageService;

    public ProcessorHandler(TaskProcessorService taskProcessorService, TaskAverageService taskAverageService) {
        this.taskProcessorService = taskProcessorService;
        this.taskAverageService = taskAverageService;
    }

    public Mono<?> handle(TaskContext context) {
        log.info("Handling process for {}", context.workflowType.name());

        return switch (context.workflowType) {
            case PROCESS_TASK -> taskProcessorService.process(context);
            case CALCULATE_AVERAGE_DURATION -> taskAverageService.process(context);
            default -> throw new IllegalArgumentException("Unknown workflow type: " + context.workflowType);
        };
    }
}
