
package com.coolplanet.task.application.service;

import com.coolplanet.task.application.processor.ProcessingStepFactory;
import com.coolplanet.task.domain.entity.TaskEntity;
import com.coolplanet.task.domain.exception.ProcessingException;
import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.domain.model.TaskResponse;
import com.coolplanet.task.infrastructure.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * TaskProcessorService is a service class responsible for processing tasks
 * within a non-blocking reactive workflow. It implements the {@code TaskService}
 * interface and handles the execution of a list of defined processing steps
 * and the management of task data in the reactive repository.
 *
 * This service performs the following key operations:
 * - Delegates task-specific steps to a factory-provided list of {@code ProcessingStep} instances.
 * - Retrieves or creates a {@code TaskEntity} from the repository for tracking task execution.
 * - Aggregates task execution duration by upserting data into the repository.
 * - Handles errors in task processing and maps them to a custom exception.
 *
 * The processing of steps and repository interactions is performed in a
 * non-blocking manner, leveraging Project Reactor's reactive APIs.
 *
 * This class is intended to support scenarios where asynchronous and distributed
 * task processing logic must be handled efficiently while ensuring data consistency
 * in a concurrent execution context.
 */
@Slf4j
@Service
public class TaskProcessorService implements TaskService<TaskResponse, TaskContext> {

    private final TaskRepository repository;
    private final ProcessingStepFactory stepFactory;

    public TaskProcessorService(TaskRepository repository, ProcessingStepFactory stepFactory) {
        this.repository = repository;
        this.stepFactory = stepFactory;
    }

    public Mono<TaskResponse> process(TaskContext taskContext) {
        TaskDTO taskDTO = (TaskDTO) taskContext.request;

        return Mono.defer(() -> {
            log.info("Processing task : {} {} ",taskDTO.taskId(), taskDTO.totalDurationMs());

            TaskEntity taskEntity = new TaskEntity(taskDTO.taskId(), taskDTO.totalDurationMs());

            // Step execution is non-blocking but offloaded to boundedElastic
            return Mono.fromRunnable(() -> runSteps(taskContext))
                    .subscribeOn(Schedulers.boundedElastic())
                    // Using upsert to ensure atomicity of total duration and counter updates
                    .then(repository.upsert(taskEntity.getTaskId(), taskEntity.getTotalDurationMs()))
                    .doOnNext(totalDurationMs ->
                            log.info("New total duration: {}", totalDurationMs)
                    ).thenReturn(new TaskResponse("OK"));
        }).onErrorMap(ex -> {
            log.error("Error processing taskDTO {}", taskDTO.taskId(), ex);
            String errorMessage = String.format("Failed to process taskDTO: %s, %s", taskDTO.taskId(),  ex.getMessage());
            return new ProcessingException(errorMessage);
        });
    }

    private void runSteps(TaskContext taskContext) {
        stepFactory.getSteps().forEach(step -> step.execute(taskContext.request));
    }

}
