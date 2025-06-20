package com.coolplanet.task.infrastructure;

import com.coolplanet.task.domain.entity.TaskEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Test class for verifying the behavior of {@link TaskRepository}.
 * This class uses R2DBC to interact with a PostgreSQL database and leverages Testcontainers
 * to run tests within an isolated and reproducible environment.
 *
 * An embedded PostgreSQL container is used for testing to simulate interactions with
 * a real database. The repository operations are validated with test scenarios,
 * including inserts, updates, concurrent operations, and error cases.
 *
 * It covers the following test cases:
 * - Saving and retrieving a task entity.
 * - Handling concurrent upserts on the same task entity.
 * - Verifying results for non-existent task queries.
 * - Updating properties of an existing task entity.
 *
 * The database schema and data consistency are initialized before each test
 * using the {@link DatabaseClient} to ensure a clean state.
 *
 * Annotations:
 * - {@link DataR2dbcTest}: Indicates that the test focuses on R2DBC components.
 * - {@link Testcontainers}: Enables the use of Testcontainers to manage the lifecycle of containers.
 * - {@link Import}: Imports the configuration setup required for the test.
 */
@DataR2dbcTest
@Testcontainers
@Import(R2dbcPostgresConfiguration.class)
class TaskRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("task")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%d/%s",
                        postgres.getHost(),
                        postgres.getMappedPort(5432),
                        postgres.getDatabaseName())
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        // Clean up the table before each test
        databaseClient.sql("DELETE FROM task").then().block();
    }

    @Test
    void shouldSaveAndRetrieveTask() {
        TaskEntity task = new TaskEntity("test-task-1", 1000L);

        StepVerifier.create(taskRepository.upsert(task.getTaskId(), task.getTotalDurationMs())
                .then(taskRepository.findById(task.getTaskId())))
                .expectNextMatches(found -> found.getTaskId().equals("test-task-1") &&
                        found.getTotalDurationMs() == 1000L &&
                        found.getCounter() == 1L
                )
                .verifyComplete();
    }

    @Test
    void shouldHandleConcurrentUpserts() {
        String taskId = "concurrent-task";
        long duration = 1000L;

        // Perform 5 concurrent upserts
        StepVerifier.create(
                        Flux.range(0, 5)
                                .flatMap(i -> taskRepository.upsert(taskId, duration))
                                .then(taskRepository.findById(taskId))
                )
                .expectNextMatches(task ->
                        task.getTaskId().equals(taskId) &&
                                task.getTotalDurationMs() == 5000L && // 5 * 1000
                                task.getCounter() == 5L
                )
                .verifyComplete();
    }

    @Test
    void shouldHandleNonExistentTask() {
        StepVerifier.create(taskRepository.findById("non-existent"))
                .verifyComplete();
    }

    @Test
    void shouldUpdateExistingTask() {
        String taskId = "update-test";

        // First insertion
        StepVerifier.create(taskRepository.upsert(taskId, 1000L)
                        .then(taskRepository.findById(taskId)))
                .expectNextMatches(task ->
                        task.getTotalDurationMs() == 1000L &&
                                task.getCounter() == 1L
                )
                .verifyComplete();

        // Second update
        StepVerifier.create(taskRepository.upsert(taskId, 2000L)
                        .then(taskRepository.findById(taskId)))
                .expectNextMatches(task ->
                        task.getTotalDurationMs() == 3000L && // 1000 + 2000
                                task.getCounter() == 2L
                )
                .verifyComplete();
    }
}
