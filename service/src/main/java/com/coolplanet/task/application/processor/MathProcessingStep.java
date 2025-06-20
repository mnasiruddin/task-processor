
package com.coolplanet.task.application.processor;

import com.coolplanet.task.domain.model.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The MathProcessingStep class performs a mathematical operation as part of a
 * processing workflow. It implements the ProcessingStep interface and executes
 * the processing logic using a provided request object, which is expected to
 * be of type TaskDTO.
 */
public class MathProcessingStep implements ProcessingStep {
    private static final Logger log = LoggerFactory.getLogger(MathProcessingStep.class);

    /**
     * Executes the processing logic for a given task by performing a mathematical
     * operation (logarithm) on the task's total duration. This method expects the
     * provided request object to be an instance of TaskDTO.
     *
     * Logs the computation result along with the associated task identifier.
     *
     * @param request the input object containing task details, expected to be an instance of TaskDTO
     */
    @Override
    public void execute(Object request) {
        TaskDTO taskDTO = (TaskDTO) request;
        double result = Math.log(taskDTO.totalDurationMs() + 1);
        log.info("Math step for task {}: computed log = {}", taskDTO.taskId(), result);
    }
}
