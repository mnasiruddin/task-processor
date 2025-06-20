package com.coolplanet.task.adapter;

import com.coolplanet.task.application.service.TaskProcessorService;
import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.domain.model.WorkflowType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * KafkaConsumer is a component responsible for consuming messages from a Kafka topic
 * and delegating the processing of these messages to the {@code TaskProcessorService}.
 * This class listens to specific Kafka topics using the Spring Kafka framework and handles
 * messages asynchronously.
 *
 * Responsibilities:
 * - Listens to the "task-events" Kafka topic as part of the "task-consumer-group".
 * - Converts incoming messages into {@code TaskDTO} objects.
 * - Constructs a {@code TaskContext} with the received {@code TaskDTO}, specifying
 *   the workflow type as {@code PROCESS_TASK}, and delegates it to {@code TaskProcessorService}
 *   for processing.
 * - Logs the received task data and initiates the reactive task processing workflow.
 *
 * Dependencies:
 * - {@code TaskProcessorService}: Used to process tasks based on the constructed
 *   {@code TaskContext}.
 *
 * Method Details:
 * - {@code listen(TaskDTO task)}:
 *   Automatically invoked when a message is published to the subscribed Kafka topic.
 *   Logs the task information, constructs the processing context, and delegates
 *   task processing to the underlying service.
 *
 * Annotations:
 * - {@code @Slf4j}: Enables logging within the class.
 * - {@code @Component}: Marks this class as a Spring managed component eligible
 *   for dependency injection.
 * - {@code @KafkaListener}: Registers the {@code listen} method as a Kafka message
 *   listener for the specified topic and consumer group.
 *
 * This class is designed to integrate seamlessly with messaging systems in applications
 * that require task-based event-driven processing. The processing logic itself is
 * encapsulated in the {@code TaskProcessorService}, ensuring separation of concerns
 * and maintainability.
 */
@Slf4j
@Component
public class KafkaConsumer {

    private final TaskProcessorService taskProcessorService;
    private final ObjectMapper objectMapper;

    public KafkaConsumer(TaskProcessorService taskProcessorService, ObjectMapper objectMapper) {
        this.taskProcessorService = taskProcessorService;
        this.objectMapper = objectMapper;
        log.info("KafkaConsumer initialized");
    }

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(String taskJson) throws JsonProcessingException {
            log.info("Raw JSON received from Kafka: {}", taskJson);

            TaskDTO task = objectMapper.readValue(taskJson, TaskDTO.class);

            TaskContext taskContext = TaskContext.builder()
                    .workflowType(WorkflowType.PROCESS_TASK)
                    .request(task)
                    .build();

            taskProcessorService.process(taskContext)
                    .subscribe();
    }
}

