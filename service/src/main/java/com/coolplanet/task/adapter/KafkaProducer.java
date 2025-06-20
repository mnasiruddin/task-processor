package com.coolplanet.task.adapter;

import com.coolplanet.task.domain.exception.ProcessingException;
import com.coolplanet.task.domain.model.TaskDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * KafkaProducer is a Spring component responsible for producing and sending
 * messages to a Kafka topic.
 *
 * Responsibilities:
 * - Publishes task-related events encapsulated in {@code TaskDTO} to the "task-events" Kafka topic
 *   using the provided {@code KafkaTemplate}.
 * - Logs relevant information about the messages being sent for observability.
 * - Returns a confirmation string upon successful task message publishing.
 *
 * Dependencies:
 * - {@code KafkaTemplate<String, TaskDTO>}: A template for sending messages to Kafka topics,
 *   pre-configured for task message publication.
 *
 * Method Details:
 * - {@code sendTask(TaskDTO task)}:
 *   Asynchronously sends a {@code TaskDTO} to the "task-events" Kafka topic, using the task's unique
 *   identifier as the key. Logs the task being sent and returns a confirmation string upon
 *   completion of the send operation.
 *
 * Annotations:
 * - {@code @Slf4j}: Enables enhanced logging functionality within the class.
 * - {@code @Component}: Marks this class as a Spring-managed component, making it eligible
 *   for dependency injection.
 *
 * Use Cases:
 * - The KafkaProducer is designed to be invoked by other application services or components
 *   to propagate task-related events to downstream consumers via Kafka messaging.
 */
@Slf4j
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic}")
    private final String topic = "task-events";

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        log.info("KafkaProducer initialized");
    }

    public Mono<String> sendTask(TaskDTO task) {
        log.info("Sending task to Kafka: {}", task);
        return Mono.fromRunnable(() -> {
                    try {
                        String taskJson = objectMapper.writeValueAsString(task);
                        kafkaTemplate.send(topic, task.taskId(), taskJson);
                    } catch (JsonProcessingException jsonProcessingException) {
                        throw new ProcessingException(jsonProcessingException.getMessage());
                    }
                })
                .thenReturn("Task sent to Kafka");
    }
}
