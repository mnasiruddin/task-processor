package com.coolplanet.task.application.util;

import com.coolplanet.task.domain.model.TaskDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskDTOSerializerTest {

    /**
     * This class tests the serialize method in the TaskDTOSerializer class.
     * The serialize method is responsible for converting a TaskDTO object
     * into a byte[] representation using the Jackson ObjectMapper.
     */

    @Test
    void testSerializeWithValidTaskDTO() {
        // Given
        TaskDTOSerializer serializer = new TaskDTOSerializer();
        TaskDTO taskDTO = new TaskDTO("task-123", 1000L);

        // When
        byte[] serializedData = serializer.serialize("test-topic", taskDTO);

        // Then
        assertNotNull(serializedData);
        assertTrue(serializedData.length > 0);

        // Validate the serialized content
        ObjectMapper objectMapper = new ObjectMapper();
        TaskDTO deserializedTask;
        try {
            deserializedTask = objectMapper.readValue(serializedData, TaskDTO.class);
        } catch (Exception e) {
            fail("Failed to deserialize serialized data: " + e.getMessage());
            return;
        }
        assertEquals(taskDTO.taskId(), deserializedTask.taskId());
        assertEquals(taskDTO.totalDurationMs(), deserializedTask.totalDurationMs());
    }

    @Test
    void testSerializeWithNullTaskDTO() {
        // Given
        TaskDTOSerializer serializer = new TaskDTOSerializer();

        // When & Then
        assertDoesNotThrow(() -> {
            byte[] serializedData = serializer.serialize("test-topic", null);
            assertNotNull(serializedData, "Serialized data should not be null when input is null");
        });
    }

    @Test
    void testSerializeWithNoExceptionalCase() {
        // Given
        TaskDTOSerializer serializer = new TaskDTOSerializer();

        TaskDTO invalidTaskDTO = new TaskDTO(null, -1);

        // When & Then
        assertDoesNotThrow(() -> {
            serializer.serialize("test-topic", invalidTaskDTO);
        });
    }
}