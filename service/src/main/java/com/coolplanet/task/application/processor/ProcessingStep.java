
package com.coolplanet.task.application.processor;

/**
 * Represents a step in a processing workflow. Classes implementing this interface
 * define specific logic to be executed as part of a processing pipeline.
 *
 * The `execute` method is invoked to perform the step's processing, using a generic
 * request object as input. It is the responsibility of the implementing classes to
 * interpret and handle the request object as needed.
 */
public interface ProcessingStep {
    /**
     * Executes the processing logic for a given task using the provided
     * input object. Classes implementing this method define specific operations
     * to be performed as part of a processing workflow.
     *
     * @param request the input object containing data required for processing;
     *                its exact type and structure depend on the implementing class
     */
    void execute(Object request );
}
