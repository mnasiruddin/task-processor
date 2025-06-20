package com.coolplanet.task.application;

import com.coolplanet.task.domain.model.TaskContext;
import reactor.core.publisher.Mono;

/**
 * Handler is a functional interface that defines a contract for handling
 * reactive task processing based on a given {@code TaskContext}.
 *
 * Responsibilities:
 * - Provides a generic method for handling tasks in a non-blocking, reactive manner.
 * - Accepts a specific {@code TaskContext} to determine the processing workflow.
 * - Produces a result wrapped in a reactive {@code Mono}, allowing asynchronous processing.
 *
 * Type Parameters:
 * - {@code T}: The type of the result produced by the handler.
 *
 * Design:
 * - This interface promotes a decoupled and flexible workflow execution pattern.
 * - It allows implementations to define custom processing logic by utilizing different workflows.
 * - Being a functional interface, it is suitable for lambda expressions or method references.
 *
 * Usage Scenarios:
 * - To encapsulate reactive processing logic for different workflows.
 * - To provide unified handling for tasks, enabling scalability and maintainability in reactive systems.
 *
 * Reactive Behavior:
 * - The method {@code handle} returns a {@code Mono} that represents the asynchronous result of the task.
 * - This supports reactive programming principles and enables composing various reactive workflows.
 */
@FunctionalInterface
public interface Handler {
    <T> Mono<T> handle(TaskContext taskContext);
}
