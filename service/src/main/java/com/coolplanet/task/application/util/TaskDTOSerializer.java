package com.coolplanet.task.application.util;

import com.coolplanet.task.domain.model.TaskDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class TaskDTOSerializer implements Serializer<TaskDTO> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, TaskDTO data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception exception) {
            throw new RuntimeException("Error serializing TaskDTO", exception);
        }
    }
}