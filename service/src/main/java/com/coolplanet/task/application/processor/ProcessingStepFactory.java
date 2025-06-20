
package com.coolplanet.task.application.processor;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The ProcessingStepFactory class is a component responsible for creating and
 * providing a collection of processing steps. It contains logic to assemble
 * and return a list of predefined implementations of the ProcessingStep interface.
 *
 * This factory class facilitates the encapsulation of step creation, ensuring
 * that consumers of this factory do not need to manage the individual instantiation
 * of specific processing steps. It is useful in scenarios where multiple processing
 * operations need to be applied in sequence within a workflow.
 *
 * The current implementation provides the following processing steps:
 * 1. MathProcessingStep - Performs a mathematical operation as part of a workflow.
 * 2. ExternalCallProcessingStep - Simulates an external system call as part of a workflow.
 */
@Component
public class ProcessingStepFactory {

    public static final List<ProcessingStep> PROCESSING_STEPS = List.of(new MathProcessingStep(), new ExternalCallProcessingStep());

    /**
     * Provides a list of processing steps that are part of the processing workflow.
     * The method returns a collection of predefined implementations of the
     * {@code ProcessingStep} interface, specifically:
     * 1. {@code MathProcessingStep} - Performs mathematical operations on task data.
     * 2. {@code ExternalCallProcessingStep} - Simulates external system interactions.
     *
     * @return a list containing instances of {@code MathProcessingStep}
     *         and {@code ExternalCallProcessingStep}, representing the
     *         processing steps in the workflow
     */
    public List<ProcessingStep> getSteps() {
        return PROCESSING_STEPS;
    }
}
