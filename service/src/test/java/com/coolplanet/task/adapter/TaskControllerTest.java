package com.coolplanet.task.adapter;

import com.coolplanet.task.application.ProcessorHandler;
import com.coolplanet.task.domain.exception.TaskNotFoundException;
import com.coolplanet.task.domain.model.TaskContext;
import com.coolplanet.task.domain.model.TaskDTO;
import com.coolplanet.task.domain.model.TaskResponse;
import com.coolplanet.task.infrastructure.R2dbcPostgresConfiguration;
import com.coolplanet.task.infrastructure.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for the TaskController to validate various behaviors of the task management APIs.
 * Utilizes WebTestClient for mocking HTTP calls and validating API responses.
 * Mocks the ProcessorHandler dependency to isolate unit test behavior for service layer validation.
 * All tests within this class are annotated with @Test and utilize JUnit 5 for execution.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@Import(R2dbcPostgresConfiguration.class)
class TaskControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ProcessorHandler processorHandler;

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
    void recordTask_ShouldReturnOkStatus_WhenHandlerReturnsTaskResponse() {
        String taskId = "task123";
        Long duration = 1000L;
        TaskResponse expectedResponse = new TaskResponse("OK");

        when(processorHandler.handle(any(TaskContext.class)))
                .thenReturn((Mono) Mono.just(expectedResponse));

        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/tasks")
                        .queryParam("taskId", taskId)
                        .queryParam("duration", duration)
                        .build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TaskResponse.class)
                .value(response -> assertEquals(expectedResponse.status(), response.status()));

        Mockito.verify(processorHandler).handle(any(TaskContext.class));
    }

    @Test
    void recordTask_ShouldReturnBadRequest_WhenIdIsMissing() {
        Long duration = 1000L;

        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/tasks")
                        .queryParam("duration", duration)
                        .build())
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void recordTask_ShouldReturnBadRequest_WhenDurationIsMissing() {
        String taskId = "task123";

        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/tasks")
                        .queryParam("taskId", taskId)
                        .build())
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void recordTask_ShouldReturnBadRequest_WhenPathIsInCorrect() {
        String taskId = "task123";

        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/tasks/tasks")
                        .queryParam("taskId", taskId)
                        .queryParam("duration", 100L)
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void recordTask_ShouldReturnBadRequest_WhenDurationIsNegative() {
        String taskId = "task123";

        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/tasks")
                        .queryParam("taskId", taskId)
                        .queryParam("duration", -100L)
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void taskAverage_ShouldReturnOkStatus_WhenTaskExists() {
        String taskId = "task123";
        TaskDTO expectedResponse = new TaskDTO(taskId, 500L);

        when(processorHandler.handle(any(TaskContext.class)))
                .thenReturn((Mono) Mono.just(expectedResponse));

        webClient.get()
                .uri("/tasks/{id}/average", taskId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskDTO.class)
                .value(response -> {
                    assertEquals(expectedResponse.taskId(), response.taskId());
                    assertEquals(expectedResponse.totalDurationMs(), response.totalDurationMs());
                });

        Mockito.verify(processorHandler).handle(any(TaskContext.class));
    }

    @Test
    void taskAverage_ShouldReturnNotFound_WhenTaskDoesNotExist() {
        String taskId = "nonexistent-task";

        when(processorHandler.handle(any(TaskContext.class)))
                .thenReturn(Mono.error(new TaskNotFoundException("Task not found")));

        webClient.get()
                .uri("/tasks/{taskId}/average", taskId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void taskAverage_ShouldReturnBadRequest_WhenTaskIdIsInvalid() {
        webClient.get()
                .uri("/tasks/{taskId}/average", " ")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void taskAverage_ShouldReturnNotFound_WhenUsingIncorrectPath() {
        String taskId = "task123";

        when(processorHandler.handle(any(TaskContext.class)))
                .thenReturn(Mono.error(new TaskNotFoundException("Task not found")));

        webClient.get()
                .uri("/tasks/{taskId}/average", taskId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}