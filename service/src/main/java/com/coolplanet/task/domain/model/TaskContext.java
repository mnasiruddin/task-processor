package com.coolplanet.task.domain.model;

import lombok.Builder;

/**
 * Represents the context in which a task is executed, providing metadata or additional
 * information required by the processing logic.
 *
 * A TaskContext is typically utilized in task execution workflows that involve
 * asynchronous or distributed operations. It encapsulates key information such as the
 * task request payload and the workflow type.
 *
 * Fields:
 * - request: Represents the data associated with the task being processed. It may
 *   consist of various formats depending on the task requirements, including but not
 *   limited to TaskDTO objects.
 * - workflowType: Specifies the type of workflow associated with the task, indicating
 *   how the task is processed. Possible values are defined in the WorkflowType enum,
 *   which includes options such as PROCESS_TASK and CALCULATE_AVERAGE_DURATION.
 *
 * Usage Scenarios:
 * - TaskContext is a central data structure used in processes that involve task computation,
 *   persisting updates to tasks, stepwise workflows, and handling conditions based on the workflow type.
 * - It provides a unified mechanism to carry task information and processing strategy within
 *   service and repository interactions.
 *
 * Immutability:
 * - This class is annotated with {@code @Builder}, enabling convenient, immutable creation
 *   of TaskContext instances through a fluent builder pattern.
 */
@Builder
public class TaskContext {

    public Object request;
    public WorkflowType workflowType;
}
