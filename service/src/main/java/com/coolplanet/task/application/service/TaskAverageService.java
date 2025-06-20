
package com.coolplanet.task.application.service;

import com.coolplanet.task.domain.entity.TaskEntity;
import com.coolplanet.task.domain.exception.TaskNotFoundException;
import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.infrastructure.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service implementation for calculating the average duration of a task using the provided TaskRepository.
 * Implements the TaskService interface to handle task processing operations.
 *
 * Responsibilities:
 * - Retrieves task details from the repository based on the task ID.
 * - Calculates the average duration of the task by dividing the total duration by the task counter.
 * - Throws a RecordNotFoundException if the task is not found in the repository.
 *
 * Annotations:
 * - {@code @Slf4j}: Provides logging capabilities for the service.
 * - {@code @Service}: Indicates that this class is a Spring service component.
 *
 * Constructor:
 * - Accepts a TaskRepository instance, which is used for interactions with the data source.
 *
 * Methods:
 * - {@code process(TaskContext taskContext)}:
 *      Processes the given TaskContext to compute the average duration of a specified task.
 *      Retrieves the task using the task ID from the repository. If the task is found, returns a TaskDTO
 *      containing the average duration. If not found, it raises a RecordNotFoundException.
 *
 * Dependencies:
 * - TaskRepository: Used to fetch task details from the database.
 *
 * Exceptions:
 * - RecordNotFoundException: Thrown if the task with the specified ID does not exist in the repository.
 */
@Slf4j
@Service
public class TaskAverageService implements TaskService<TaskDTO, TaskContext> {

    private final TaskRepository repository;

    public TaskAverageService(TaskRepository repository) {
        this.repository = repository;
    }

    public Mono<TaskDTO> process(TaskContext taskContext) {
        TaskDTO taskDTO = (TaskDTO) taskContext.request;
        log.info("Calculating average duration for task : {} ", taskDTO.taskId());

        TaskEntity taskEntity = new TaskEntity(taskDTO.taskId());

        return repository.findById(taskEntity.getTaskId())
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Given task is not found : " + taskDTO.taskId())))
                .map(task -> {
                    log.info("details for task {} : total duration is {} ms and has updated {} times", task.getTaskId(), task.getTotalDurationMs(), task.getCounter());
                    return new TaskDTO(task.getTaskId(), task.getTotalDurationMs() / task.getCounter());
                });
    }

}
