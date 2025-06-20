
package com.coolplanet.task.infrastructure;

import com.coolplanet.task.domain.entity.TaskEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repository interface for managing {@code TaskEntity} objects in a reactive, non-blocking
 * manner using Spring Data R2DBC. It extends the {@code ReactiveCrudRepository} interface to
 * provide basic CRUD operations and custom query methods for the "task" table.
 *
 * Key Features:
 * - Handles reactive CRUD operations for `TaskEntity` objects identified by a {@code String} primary key.
 * - Provides a custom upsert query to insert or update a task's total duration and increment its counter atomically.
 *
 * Custom Query Methods:
 * - {@code upsert(String taskId, Long totalDuration)}:
 *        Performs an atomic insert or update operation on the "task" table. If the specified
 *        {@code taskId} already exists, the method increments the existing row's {@code total_duration_ms}
 *        and {@code counter}. If the {@code taskId} does not exist, it inserts a new row with an initial
 *        total duration and a counter value of 1.
 *
 * Annotations:
 * - {@code @Repository}: Indicates that this interface is a repository in the Spring Data model.
 * - {@code @Modifying}: Marks the custom query method as a modifying (insert/update) operation.
 * - {@code @Query}: Specifies the SQL query for the custom method.
 */
@Repository
public interface TaskRepository extends ReactiveCrudRepository<TaskEntity, String> {

    /**
     * Performs an atomic upsert operation on the "task" table. If a task with the specified {@code taskId}
     * already exists, this method increments its {@code total_duration_ms} and its {@code counter} by 1.
     * If the task does not exist, a new record is inserted with {@code total_duration_ms} initialized
     * to the provided value and the {@code counter} set to 1.
     *
     * @param taskId the unique identifier of the task to be inserted or updated
     * @param totalDurationMs the duration to be added to the {@code total_duration_ms} of the task
     * @return a {@code Mono<Long>} containing the new total duration ({@code total_duration_ms}) after the operation
     */
    @Query("""
        INSERT INTO task AS t (task_id, total_duration_ms, counter)
         VALUES (:taskId, :totalDurationMs, 1)
         ON CONFLICT (task_id)
         DO UPDATE SET
           total_duration_ms = t.total_duration_ms + EXCLUDED.total_duration_ms,
           counter = t.counter + 1
         RETURNING total_duration_ms;
        """)
    Mono<Long> upsert(@Param("taskId") String taskId, @Param("totalDurationMs") Long totalDurationMs);

}
