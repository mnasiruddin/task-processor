package com.coolplanet.task.application.processor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessingStepFactoryTest {

    @Test
    public void testGetSteps_ReturnsNonEmptyList() {
        // Arrange
        ProcessingStepFactory factory = new ProcessingStepFactory();

        // Act
        List<ProcessingStep> steps = factory.getSteps();

        // Assert
        assertNotNull(steps, "The returned list should not be null.");
        assertEquals(2, steps.size(), "The number of processing steps should be 2.");
    }

    @Test
    public void testGetSteps_ContainsMathProcessingStep() {
        // Arrange
        ProcessingStepFactory factory = new ProcessingStepFactory();

        // Act
        List<ProcessingStep> steps = factory.getSteps();

        // Assert
        boolean containsMathProcessingStep = steps.stream()
                .anyMatch(step -> step instanceof MathProcessingStep);
        assertTrue(containsMathProcessingStep, "The list should contain an instance of MathProcessingStep.");
    }

    @Test
    public void testGetSteps_ContainsExternalCallProcessingStep() {
        // Arrange
        ProcessingStepFactory factory = new ProcessingStepFactory();

        // Act
        List<ProcessingStep> steps = factory.getSteps();

        // Assert
        boolean containsExternalCallProcessingStep = steps.stream()
                .anyMatch(step -> step instanceof ExternalCallProcessingStep);
        assertTrue(containsExternalCallProcessingStep, "The list should contain an instance of ExternalCallProcessingStep.");
    }

}