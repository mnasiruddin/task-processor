# Task Processing Service

## 🏍️ Overview

A high-performance, reactive task processing service built with Spring WebFlux and R2DBC. It features task duration tracking, Kafka integration, and statistical analysis while ensuring concurrency safety and asynchronous toggling via feature flags.

🔗 Project Link: [https://github.com/mnasiruddin/task-processor](https://github.com/mnasiruddin/task-processor)

---

## ✨ Features

| Category      | Capabilities                                                                |
| ------------- | --------------------------------------------------------------------------- |
| Reactive      | WebFlux-based endpoints, Project Reactor, non-blocking DB with R2DBC        |
| Data Layer    | PostgreSQL integration, atomic upserts, async persistence                   |
| Messaging     | Kafka producer + consumer with toggle-based async/sync task dispatch        |
| Observability | Micrometer tracing, MDC, Brave integration, `X-Trace-Id` header propagation |
| Documentation | SpringDoc OpenAPI & Swagger                                                 |
| Testing       | TestContainers, JaCoCo test coverage, K6 performance benchmarks             |

---

## 🛠 Tech Stack

| Core              | Infrastructure             | Tooling                        |
| ----------------- | -------------------------- | ------------------------------ |
| Java 21           | PostgreSQL                 | Docker, Docker Compose         |
| Spring Boot 3.3.0 | Kafka (Confluent)          | K6 (via TestContainers)        |
| Spring WebFlux    | R2DBC                      | SpringDoc OpenAPI + Swagger UI |
| Project Reactor   | TestContainers             | JaCoCo                         |
| Lombok            | Feature-based async toggle | Micrometer + Brave             |

---

## 🚱 Clean Architecture Structure

```
Controller → Application → Domain
                        ↑
             Infrastructure implements ports
```

### 📂 Module Breakdown

```
task-service/
├── application/           # Business use cases, service interfaces
├── domain/                # Core business logic and entities
├── infrastructure/        # External systems (DB, Kafka, etc.)
├── adapter/               # WebFlux controllers
├── config/                # Tracing, filters, observability
├── resources/             # Config files (YAML, static)
└── TaskServiceApplication.java
```

---

## 🧩 Design Choices

This project was crafted with extensibility and clarity in mind. Key decisions:

| Design Area           | Description                                                                  |
| --------------------- | ---------------------------------------------------------------------------- |
| Context Object        | `TaskContext` is passed through the layers to ensure encapsulated logic      |
| Handler Chain         | `ProcessorHandler` interprets workflows and delegates to appropriate service |
| Delegation            | Each `Processor` delegates to its service (e.g., `TaskProcessorService`)     |
| Async Flexibility     | Feature flag toggles between Kafka async and direct sync service execution   |
| Factory Pattern       | Processor creation is abstracted via factory classes                         |
| Single Responsibility | Each layer adheres to SRP for better testability and scalability             |

This modular flow ensures all task operations flow cleanly from controller → context → handler → processor → service, with optional Kafka-based async persistence.

---

## 📂 Core Components

| Layer              | Classes/Responsibilities                                                        |
| ------------------ | ------------------------------------------------------------------------------- |
| **Controller**     | `TaskController` → POST `/tasks`, GET `/tasks/{taskId}/average`                 |
| **Application**    | `ProcessorHandler`, `TaskProcessorService`, `TaskAverageService`, `TaskService` |
| **Domain**         | `TaskDTO`, `TaskContext`, `WorkflowType`, `TaskResponse`                        |
| **Infrastructure** | `TaskRepository`, `TaskEntity`, Kafka consumer/producer setup                   |

---

## 📂 Database Schema

```sql
CREATE TABLE IF NOT EXISTS task (
  id SERIAL,
  task_id VARCHAR(255) NOT NULL PRIMARY KEY,
  total_duration_ms BIGINT NOT NULL,
  counter BIGINT NOT NULL
);
```

---

## ✅ Kafka Integration & Feature Toggle

The application supports **sync** and **async** task processing modes via Kafka, configurable using `application.yml`:

```yaml
features:
  task:
    async-enabled: true                # Toggle async Kafka mode
```

| Component       | Responsibility                                                  |
| --------------- | --------------------------------------------------------------- |
| `KafkaProducer` | Sends `TaskDTO` to Kafka topic if feature flag is enabled       |
| `KafkaConsumer` | Listens on topic, deserializes `TaskDTO`, calls `TaskProcessor` |
| `FeatureConfig` | Switches sync vs async dispatch based on config                 |

✅ **When `enabled: false`**, the system bypasses Kafka and calls the DB directly.

---

## ✅ Test Coverage

| Metric        | Value  |
| ------------- |--------|
| Line Coverage | \~88%  |
| Tool          | JaCoCo |

📍 Report: `target/site/jacoco/index.html`

```bash
mvn clean verify
```

![test coverage report](/docs/testcoverage/test-coverage.png)

---

## 🚀 Performance Testing

| Scenario | Details                                  |
| -------- | ---------------------------------------- |
| Load     | 100 concurrent users, 1 min, POST /tasks |
| Results  | 100 req/sec, avg 45ms, error rate <1%    |
| Tooling  | K6 via TestContainers                    |

![perf test metrics](/docs/testcoverage/perf-test-metrics.png)
[Report JSON](/perf-test/k6-report.json)

---

## 📡 Distributed Tracing

![tracing](/docs/tracing-info.png)

| Feature            | Description                                   |
| ------------------ | --------------------------------------------- |
| Header Propagation | `X-Trace-Id` tracked through MDC & logs       |
| Tracer             | Brave tracer via Micrometer                   |
| Config             | `TracingConfig`, `TraceHeaderFilter`          |
| Log Format         | `[traceId=%X{traceId} spanId=%X{spanId}] ...` |

---

## ▶️ Running the Application

### Prerequisites

* JDK 21
* Docker + Docker Compose
* Maven

```
Note: 
Below Error means you are using either java 23/24, use java 21
java: java.lang.ExceptionInInitializerError
com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

### Docker Run

```bash
docker-compose up --build
```

### Manual Run (Local Dev)

```bash
# Start PostgreSQL + Kafka
docker-compose -f docker-compose-without-task-app.yml up --build

# Build App
mvn clean install

# Run
mvn -pl service spring-boot:run
```

---

## ✅ Health Endpoints

* [Actuator](http://localhost:8080/actuator)
* [Info](http://localhost:8080/actuator/info)
* [Health](http://localhost:8080/actuator/health)

---

## 📖 API Documentation

* [Swagger UI](http://localhost:8080/swagger-ui/index.html)
* [OpenAPI JSON](http://localhost:8080/v3/api-docs)
* [OpenAPI YAML](http://localhost:8080/v3/api-docs.yaml)

### Key Endpoints

| Method | Endpoint                        | Description                       |
| ------ |---------------------------------| --------------------------------- |
| POST   | `/tasks?taskId=123&duration=50` | Submit task duration              |
| GET    | `/tasks/{taskId}/average`       | Fetch aggregated average duration |

---

## ⚙️ Configuration Sample

```yaml
spring:
  application:
    name: task-service
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/task
    username: postgres
    password: postgres
  kafka:
    consumer:
      ....
    producer:
      ....
features:
  task:
    async-enabled: true
```

---

## ✅ Running Tests

```bash
mvn test
```

🧪 Uses TestContainers to spin up PostgreSQL

---

## 🚦 Performance Considerations

| Aspect      | Optimizations                                                       |
| ----------- | ------------------------------------------------------------------- |
| Concurrency | Reactive pipelines, atomic upserts, async toggling via feature flag |
| Database    | Counters for aggregates, R2DBC for async I/O                        |
| Messaging   | Kafka-based async ingestion (optional)                              |

---

## 📊 Monitoring & Observability

* Actuator metrics
* Trace ID correlation in logs
* Brave & Micrometer tracing
