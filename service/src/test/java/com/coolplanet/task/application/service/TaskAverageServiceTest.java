package com.coolplanet.task.application.service;

import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.entity.TaskEntity;
import com.coolplanet.task.domain.exception.TaskNotFoundException;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.infrastructure.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

/**
 * Unit test class for TaskAverageService, used to verify the functionality of the service
 * that calculates the average task duration for a given task ID using a repository.
 * The class leverages Mockito for mocking dependencies and StepVerifier for reactive assertions.
 *
 * This test class covers the following scenarios:
 * - Verifying that average duration is correctly calculated when the task exists in the repository.
 * - Verifying that a RecordNotFoundException is thrown when the task does not exist in the repository.
 *
 * Annotations:
 * - {@code @ExtendWith(MockitoExtension.class)}: Used to initialize Mockito's extensions for dependency injection.
 *
 * Dependencies:
 * - TaskRepository: A mocked repository for fetching and persisting task data.
 * - TaskAverageService: The service being tested.
 *
 * Test Methods:
 * - {@code setUp()}: Initializes TaskAverageService before each test.
 * - {@code process_shouldCalculateAverageDuration_whenTaskExists()}:
 *        Tests that the process method calculates the average duration of an existing task.
 * - {@code process_shouldThrowException_whenTaskNotFound()}:
 *        Tests that the process method throws an exception when the task is not found.
 */
@ExtendWith(MockitoExtension.class)
class TaskAverageServiceTest {

    @Mock
    private TaskRepository repository;

    private TaskAverageService taskAverageService;

    @BeforeEach
    void setUp() {
        taskAverageService = new TaskAverageService(repository);
    }

    @Test
    void process_shouldCalculateAverageDuration_whenTaskExists() {
        String taskId = "test-id";
        TaskEntity task = new TaskEntity(taskId, 1000L, 2L);
        TaskContext context = TaskContext.builder()
                .request(new TaskDTO(taskId, 0L))
                .build();

        when(repository.findById(taskId)).thenReturn(Mono.just(task));

        StepVerifier.create(taskAverageService.process(context))
                .expectNext(new TaskDTO(taskId, 500L))
                .verifyComplete();
    }

    @Test
    void process_shouldThrowException_whenTaskNotFound() {
        String taskId = "non-existent-id";
        TaskContext context = TaskContext.builder()
                .request(new TaskDTO(taskId, 0L))
                .build();

        when(repository.findById(taskId)).thenReturn(Mono.empty());

        StepVerifier.create(taskAverageService.process(context))
                .expectError(TaskNotFoundException.class)
                .verify();
    }
}