package com.coolplanet.task.application.processor;

import com.coolplanet.task.domain.model.TaskDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

class ExternalCallProcessingStepTest {

    @Test
    void testExecute_logsSimulatedExternalCall() {
        // Arrange
        ExternalCallProcessingStep processingStep = new ExternalCallProcessingStep();
        TaskDTO taskDTO = Mockito.mock(TaskDTO.class);
        when(taskDTO.taskId()).thenReturn("12345");

        // Act
        processingStep.execute(taskDTO);

        // Assert
        // No assertion needed for the log statement; this test ensures no exceptions are thrown.
    }
}