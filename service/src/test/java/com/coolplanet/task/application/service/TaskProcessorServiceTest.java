package com.coolplanet.task.application.service;

import com.coolplanet.task.application.processor.ProcessingStepFactory;
import com.coolplanet.task.domain.entity.TaskEntity;
import com.coolplanet.task.domain.exception.ProcessingException;
import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.infrastructure.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit test class for {@link TaskProcessorService}, responsible for validating the behavior
 * of task processing, including repository interactions and execution of processing steps.
 *
 * This test is designed to validate various execution scenarios of the task processing service,
 * including successful task processing, cases where no processing steps are available, handling
 * tasks not found in the repository, and error scenarios during processing steps.
 */
@ExtendWith(MockitoExtension.class)
public class TaskProcessorServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private ProcessingStepFactory stepFactory;

    private TaskProcessorService taskProcessorService;

    @BeforeEach
    void setUp() {
        taskProcessorService = new TaskProcessorService(repository, stepFactory);
    }

    @Test
    public void testProcess_SuccessfulProcessing() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO("123", 500L);
        TaskContext taskContext = TaskContext.builder().request(taskDTO).build();

        when(repository.upsert(eq("123"), anyLong())).thenReturn(Mono.just(2000L));

        // Act & Assert
        taskProcessorService.process(taskContext)
                .as(StepVerifier::create)
                .expectNextMatches(response -> response.status().equals("OK"))
                .verifyComplete();
    }

    @Test
    public void testProcess_NoStepsInFactory() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO("124", 300L);
        TaskContext taskContext = TaskContext.builder().request(taskDTO).build();

        TaskEntity existingEntity = new TaskEntity("124", 500L);

        when(stepFactory.getSteps()).thenReturn(List.of());
        when(repository.upsert(eq("124"), anyLong())).thenReturn(Mono.empty());

        // Act & Assert
        taskProcessorService.process(taskContext)
                .as(StepVerifier::create)
                .expectNextMatches(response -> response.status().equals("OK"))
                .verifyComplete();
    }

    @Test
    public void testProcess_TaskNotFoundInRepository() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO("125", 700L);
        TaskContext taskContext = TaskContext.builder().request(taskDTO).build();

        when(stepFactory.getSteps()).thenReturn(List.of(ctx -> {
        }));
        when(repository.upsert(eq("125"), eq(700L))).thenReturn(Mono.empty());

        // Act & Assert
        taskProcessorService.process(taskContext)
                .as(StepVerifier::create)
                .expectNextMatches(response -> response.status().equals("OK"))
                .verifyComplete();
    }

    @Test
    public void testProcess_ErrorDuringStepExecution() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO("126", 200L);
        TaskContext taskContext = TaskContext.builder().request(taskDTO).build();

        // We don't need to stub repository.findById here since the error will occur before it's called

        // Act & Assert
        taskProcessorService.process(taskContext)
                .as(StepVerifier::create)
                .expectErrorMatches(throwable -> throwable instanceof ProcessingException &&
                        throwable.getMessage().contains("Failed to process taskDTO: 126, last"))
                .verify();
    }

}