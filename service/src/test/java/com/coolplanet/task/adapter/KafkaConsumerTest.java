package com.coolplanet.task.adapter;

import com.coolplanet.task.application.service.TaskProcessorService;
import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.domain.model.WorkflowType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class KafkaConsumerTest {

    private final TaskProcessorService taskProcessorService = Mockito.mock(TaskProcessorService.class);

    private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);

    @Test
    public void testConsume_withValidJson_shouldProcessTask() throws JsonProcessingException {
        // Arrange
        String taskJson = "{\"id\":1000,\"name\":\"Sample Task\"}";
        TaskDTO mockTaskDTO = new TaskDTO("Sample Task", 1000L);

        when(objectMapper.readValue(taskJson, TaskDTO.class)).thenReturn(mockTaskDTO);
        when(taskProcessorService.process(any(TaskContext.class))).thenReturn(Mono.empty());

        KafkaConsumer kafkaConsumer = new KafkaConsumer(taskProcessorService, objectMapper);

        // Act
        kafkaConsumer.consume(taskJson);

        // Assert
        ArgumentCaptor<TaskContext> contextCaptor = ArgumentCaptor.forClass(TaskContext.class);
        verify(taskProcessorService).process(contextCaptor.capture());

        TaskContext capturedContext = contextCaptor.getValue();
        assertEquals(WorkflowType.PROCESS_TASK, capturedContext.workflowType);
        assertEquals(mockTaskDTO, capturedContext.request);
    }

    @Test
    public void testConsume_withInvalidJson_shouldThrowException() throws JsonProcessingException {
        // Arrange
        String invalidJson = "invalid json";

        when(objectMapper.readValue(invalidJson, TaskDTO.class)).thenThrow(JsonProcessingException.class);

        KafkaConsumer kafkaConsumer = new KafkaConsumer(taskProcessorService, objectMapper);

        // Act & Assert
        assertThrows(JsonProcessingException.class, () -> kafkaConsumer.consume(invalidJson));
        verifyNoInteractions(taskProcessorService);
    }

}