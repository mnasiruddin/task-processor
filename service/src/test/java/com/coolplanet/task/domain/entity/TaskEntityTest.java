package com.coolplanet.task.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for verifying the behavior and functionality of the TaskEntity class.
 * It covers the instantiation of TaskEntity instances, modification of its properties,
 * and calculation of averages based on provided data.
 */
public class TaskEntityTest {

    @Test
    public void testCreateWithInitialDuration() {
        TaskEntity task = new TaskEntity("task1", 1000L);
        assertEquals("task1", task.getTaskId());
        assertEquals(1000L, task.getTotalDurationMs());
        assertEquals(0L, task.getCounter());
    }

    @Test
    public void testCreateWithoutInitialDuration() {
        TaskEntity task = new TaskEntity("task1");
        assertEquals("task1", task.getTaskId());
        assertEquals(0L, task.getTotalDurationMs());
        assertEquals(0L, task.getCounter());
    }

    @Test
    public void testAddToTotalDuration() {
        TaskEntity task = new TaskEntity("task1", 1000L);
        assertEquals(1000L, task.getTotalDurationMs());
    }

    @Test
    public void testAverageCalculation() {
        TaskEntity task = new TaskEntity("task1", 1000L);
        task.setCounter(1L);
        assertEquals(1000.0, task.average());
    }

    @Test
    public void testAverageWithZeroCounter() {
        TaskEntity task = new TaskEntity("task1");
        assertEquals(0.0, task.average());
    }
}
