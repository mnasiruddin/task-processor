package com.coolplanet.task.application.service;

import reactor.core.publisher.Mono;

/**
 * Functional interface representing a generic task service capable of processing a context
 * and producing a result. This interface defines a single abstract method, making it suitable
 * for use with lambda expressions or method references.
 *
 * Responsibilities:
 * - Provides a contract for implementing reactive task processing operations.
 * - Accepts a context of a generic type for processing.
 * - Produces a result of a generic type wrapped in a reactive {@code Mono}.
 *
 * Design:
 * - The generic type {@code T} represents the type of result produced by the {@code process} method.
 * - The generic type {@code U} represents the type of context input to be processed.
 *
 * Usage Scenarios:
 * - To build reactive, non-blocking services where tasks or operations need to be processed
 *   based on specific input contexts.
 * - To create implementations handling different task processing logic, such as fetching data,
 *   performing computations, or saving results.
 *
 * Implementations:
 * - Custom implementations can define the processing logic for specific tasks, such as interaction
 *   with a data repository, executing workflows, or performing calculations.
 *
 * Reactive Behavior:
 * - The {@code process} method returns a {@code Mono} representing the asynchronous
 *   result of the processing. This promotes non-blocking workflows and supports reactive patterns.
 *
 * @param <T> The type of result produced by the task processing.
 * @param <U> The type of context input used for task processing.
 */
@FunctionalInterface
public interface TaskService<T, U> {

    Mono<T> process(U context);
}
