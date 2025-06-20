package com.coolplanet.task.application;

import com.coolplanet.task.application.service.TaskAverageService;
import com.coolplanet.task.application.service.TaskProcessorService;
import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.domain.model.TaskResponse;
import com.coolplanet.task.domain.model.WorkflowType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@code ProcessorHandler} class, verifying behavior of its {@code handle} method
 * under different conditions and workflow types.
 *
 * Methods in this test class cover workflows related to task processing and average calculation,
 * ensuring the correctness of interactions with the associated services.
 *
 * Test cases include:
 * - Verifying that the {@code TaskProcessorService} is invoked when the workflow type is {@code PROCESS_TASK}.
 * - Verifying that the {@code TaskAverageService} is invoked when the workflow type is {@code CALCULATE_AVERAGE_DURATION}.
 * - Ensuring an exception is thrown for unknown or null workflow types.
 */
class ProcessorHandlerTest {

    @Test
    void shouldCallProcessorServiceWhenWorkflowTypeIsProcessTask() {
        // Arrange
        TaskProcessorService taskProcessorService = Mockito.mock(TaskProcessorService.class);
        TaskAverageService taskAverageService = Mockito.mock(TaskAverageService.class);
        ProcessorHandler processorHandler = new ProcessorHandler(taskProcessorService, taskAverageService);

        TaskContext context = TaskContext.builder()
                .workflowType(WorkflowType.PROCESS_TASK)
                .request(new Object())
                .build();

        when(taskProcessorService.process(any(TaskContext.class))).thenReturn(Mono.just(new TaskResponse("OK")));

        // Act
        processorHandler.handle(context).block();

        // Assert
        Mockito.verify(taskProcessorService).process(any(TaskContext.class));
    }

    @Test
    void shouldCallAverageServiceWhenWorkflowTypeIsCalculateAverageDuration() {
        // Arrange
        TaskProcessorService taskProcessorService = Mockito.mock(TaskProcessorService.class);
        TaskAverageService taskAverageService = Mockito.mock(TaskAverageService.class);
        ProcessorHandler processorHandler = new ProcessorHandler(taskProcessorService, taskAverageService);

        TaskContext context = TaskContext.builder()
                .workflowType(WorkflowType.CALCULATE_AVERAGE_DURATION)
                .request(new Object())
                .build();

        when(taskAverageService.process(any(TaskContext.class))).thenReturn(Mono.just(new TaskDTO("task-123", 1000L)));

        // Act
        processorHandler.handle(context).block();

        // Assert
        Mockito.verify(taskAverageService).process(any(TaskContext.class));
    }

    @Test
    void shouldThrowExceptionWhenWorkflowTypeIsUnknown() {
        // Arrange
        TaskProcessorService taskProcessorService = Mockito.mock(TaskProcessorService.class);
        TaskAverageService taskAverageService = Mockito.mock(TaskAverageService.class);
        ProcessorHandler processorHandler = new ProcessorHandler(taskProcessorService, taskAverageService);

        TaskContext context = TaskContext.builder()
                .workflowType(WorkflowType.INVALID_WORKFLOW) // Invalid workflow
                .request(new Object())
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> processorHandler.handle(context).block());
    }

    @Test
    void shouldThrowExceptionWhenTaskContextIsNull() {
        // Arrange
        TaskProcessorService taskProcessorService = Mockito.mock(TaskProcessorService.class);
        TaskAverageService taskAverageService = Mockito.mock(TaskAverageService.class);
        ProcessorHandler processorHandler = new ProcessorHandler(taskProcessorService, taskAverageService);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> processorHandler.handle(null).block());
    }

}