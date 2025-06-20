package com.coolplanet.task.domain.model;

/**
 * Enum representing the types of workflows supported by the application.
 *
 * WorkflowType is used to define the specific processing strategy or procedure
 * that should be applied to a given task. Each enum constant represents a distinct
 * type of workflow, which informs the application's services or handlers how to
 * process the task or request.
 *
 * Enum Constants:
 * - PROCESS_TASK: Represents a workflow for processing a task. This is typically used
 *   for scenarios where a task needs to undergo specific operations or transformations.
 * - CALCULATE_AVERAGE_DURATION: Represents a workflow for calculating the average
 *   duration of tasks. This is utilized in scenarios where aggregate duration metrics
 *   need to be computed for a set of tasks.
 *
 * Usage Scenarios:
 * - WorkflowType is commonly referenced in {@code TaskContext} objects to indicate
 *   the type of workflow to be executed.
 * - It is used in service and handling logic to determine and route the appropriate
 *   processing strategy for tasks.
 *
 * Design Considerations:
 * - This enum ensures type safety and consistency when specifying workflows throughout
 *   the application.
 * - It provides a clean and extensible way to represent various workflow types, making
 *   the task processing logic scalable and maintainable.
 */
public enum WorkflowType {

    PROCESS_TASK,
    CALCULATE_AVERAGE_DURATION, INVALID_WORKFLOW;
}
