package com.coolplanet.task.adapter;

import com.coolplanet.task.domain.exception.ProcessingException;
import com.coolplanet.task.domain.model.TaskDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class KafkaProducerTest {

    private final KafkaTemplate<String, String> kafkaTemplate = Mockito.mock(KafkaTemplate.class);
    private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
    private final KafkaProducer kafkaProducer = new KafkaProducer(kafkaTemplate, objectMapper);

    @Test
    void sendTask_ShouldSendTaskSuccessfully() throws JsonProcessingException {
        // Arrange
        TaskDTO task = new TaskDTO("task-123", 1000L);
        String taskJson = "{\"taskId\":\"task-123\",\"totalDurationMs\":1000}";
        Mockito.when(objectMapper.writeValueAsString(task)).thenReturn(taskJson);

        // Act
        Mono<String> result = kafkaProducer.sendTask(task);

        // Assert
        StepVerifier.create(result)
                .expectNext("Task sent to Kafka")
                .verifyComplete();

        Mockito.verify(kafkaTemplate).send("task-events", "task-123", taskJson);
        Mockito.verify(objectMapper).writeValueAsString(task);
    }

    @Test
    void sendTask_ShouldThrowProcessingException_WhenJsonProcessingFails() throws JsonProcessingException {
        // Arrange
        TaskDTO task = new TaskDTO("task-123", 1000L);
        Mockito.when(objectMapper.writeValueAsString(task)).thenThrow(new JsonProcessingException("Serialization error") {
        });

        // Act & Assert
        Mono<String> result = kafkaProducer.sendTask(task);

        StepVerifier.create(result)
                .expectError(ProcessingException.class)
                .verify();

        Mockito.verify(objectMapper).writeValueAsString(task);
        Mockito.verifyNoInteractions(kafkaTemplate);
    }
}