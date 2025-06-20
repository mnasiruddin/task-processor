
package com.coolplanet.task.application.processor;

import com.coolplanet.task.domain.model.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ExternalCallProcessingStep class simulates an external call as part of a
 * processing workflow. It implements the ProcessingStep interface and executes
 * the processing logic using a provided request object, which is expected to
 * be of type TaskDTO.
 *
 * During execution, the class logs the task identifier, simulating an operation
 * that integrates with an external system or dependency for further task processing.
 * This class is typically used in workflows that necessitate interaction with external
 * services or systems during a processing pipeline.
 */
public class ExternalCallProcessingStep implements ProcessingStep {
    private static final Logger log = LoggerFactory.getLogger(ExternalCallProcessingStep.class);

    /**
     * Executes the processing logic for a given task by simulating an external system call.
     * The provided request object is expected to be of type TaskDTO.
     *
     * Logs the task identifier to indicate the simulated operation.
     *
     * @param request the input object containing task details, expected to be an instance of TaskDTO
     */
    @Override
    public void execute(Object request) {
        TaskDTO taskDTO = (TaskDTO) request;
        log.info("Simulated external call for task {}", taskDTO.taskId());
    }
}
