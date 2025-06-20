# Task Processing Service

## 🛍️ Overview

A high-performance, reactive task processing service built with Spring WebFlux and R2DBC. This service handles task duration tracking and provides statistical analysis capabilities while ensuring thread-efficient processing and data consistency under concurrent operations.

🔗 Project Link: [https://github.com/mnasiruddin/task-processor](https://github.com/mnasiruddin/task-processor)

---

## ✨ Features

* ⚡ Reactive endpoints using Spring WebFlux
* 🧵 Non-blocking database operations with R2DBC
* 🔀 Concurrent task processing with atomic operations
* ⏱ Task duration tracking and statistics
* 📂 PostgreSQL database integration
* ✅ Comprehensive test coverage using TestContainers

---

## 🛠 Tech Stack

* **Java 21**
* **Spring Boot 3.3.0**
* **Spring WebFlux** – Reactive web framework
* **R2DBC** – Reactive database connectivity
* **PostgreSQL** – Primary database
* **Project Reactor** – Reactive programming library
* **Lombok** – Boilerplate code reduction
* **TestContainers** – Integration testing with real databases
* **SpringDoc OpenAPI** – API documentation

---

## 🚱 Clean Architecture Structure

The project follows **Clean Architecture** principles:

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
├── infrastructure/        # External systems (DB, messaging)
├── adapter/               # WebFlux controllers
├── config/                # Tracing, filters, observability
├── resources/             # Config files (YAML, static)
└── TaskServiceApplication.java
```

### ✅ Layer Roles

| Layer              | Responsibility                                         |
| ------------------ | ------------------------------------------------------ |
| **Domain**         | Business rules, pure Java, no Spring dependency        |
| **Application**    | Orchestrates use cases, defines interfaces             |
| **Infrastructure** | Implements external systems (PostgreSQL, Kafka, Redis) |
| **Adapter**        | Exposes REST APIs using WebFlux                        |
| **Config**         | Spring beans, interceptors, tracing, actuator          |

---

## 🗂 Core Components

### 🔌 Controllers

* `TaskController` – Exposes REST endpoints

  * POST `/tasks` – Record new task
  * GET `/tasks/{taskId}/average` – Get average duration

### 🧠 Application Layer

* `ProcessorHandler` – Routes based on workflow type
* `TaskProcessorService` – Records & processes durations
* `TaskAverageService` – Computes task averages
* `TaskService` – Interface for service contracts

### 📟 Domain Model

* `TaskContext` – Context for task operations
* `TaskDTO` – Input data
* `TaskResponse` – Output response
* `WorkflowType` – Enum of supported workflows

### 📂 Infrastructure

* `TaskRepository` – R2DBC database interaction
* `TaskEntity` – Database table mapping

---

## 📂 Database Schema

```
CREATE TABLE IF NOT EXISTS task (
  id SERIAL,
  task_id VARCHAR(255) NOT NULL PRIMARY KEY,
  total_duration_ms BIGINT NOT NULL,
  counter BIGINT NOT NULL
);
```

---

## ✅ Test Coverage

* Line coverage: \~90% via JaCoCo
* Report location: `target/site/jacoco/index.html`

![test coverage report](/docs/testcoverage/test-coverage.png)

To generate:

```bash
mvn clean verify
```

---

## 🚀 Performance Testing Metrics

### 🔍 Load Test: `PerformanceTest.java`

**Scenarios:**

* 100 concurrent users
* 1-minute duration
* POST /tasks

**Results:**

* 📈 100 req/sec throughput
* 🕒 Avg response: 45ms
* ❌ Error rate: <1%

![perf test metrics](/docs/testcoverage/perf-test-metrics.png)

[result.json](/perf-test/k6-report.json)

**Tools:** K6 + Test containers

---

## 📡 Distributed Tracing

The app uses **Micrometer + Brave** for observability and tracing.

![tracing](/docs/tracing-info.png)

### 🔍 Features:

* Header propagation: `X-Trace-Id`
* MDC logging context
* Span lifecycle for HTTP calls

### 🔧 Key Classes

* `TracingConfig` – Sets up Brave tracer + MDC
* `TraceHeaderFilter` – Extracts/propagates trace headers

**Log Format:**

```
[traceId=%X{traceId} spanId=%X{spanId}] yyyy-MM-dd HH:mm:ss [thread] LEVEL logger - message
```

---

## ▶️ Running the Application

### Prerequisites

* JDK 21
* Docker + Docker Compose
* Maven

### 💻 Local Dockerized Run

```bash
cd task-processor
docker-compose up --build
```

### 💻 Run App Outside Docker

```bash
# Start DB
docker-compose -f docker-compose-postgres-only.yml up --build

# Build JAR
mvn clean install

# Run app
mvn -pl service spring-boot:run
```

---

## ✅ Health Check

* [Actuator Root](http://localhost:8080/actuator)
* [Info](http://localhost:8080/actuator/info)
* [Health](http://localhost:8080/actuator/health)

---

## 📖 API Documentation

* [Swagger UI](http://localhost:8080/swagger-ui/index.html)
* [OpenAPI JSON](http://localhost:8080/v3/api-docs)
* [OpenAPI YAML](http://localhost:8080/v3/api-docs.yaml)

### 📌 Endpoints

**POST /tasks**

* Params: `id`, `duration`
* Returns: `202 ACCEPTED`

**GET /tasks/{taskId}/average**

* Returns: average duration

---

## ⚙️ Configuration

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/taskdb
    username: postgres
    password: postgres
```

---

## ✅ Running Tests

```bash
mvn test
```

* Integration tests auto-start PostgreSQL via TestContainers

---

## 🚦 Performance Considerations

### ⚙️ Concurrency

* Atomic DB upserts
* Non-blocking reactive pipelines

### 📁 DB Efficiency

* Accumulate stats via counters
* Use R2DBC for async I/O

---

## 📊 Monitoring & Observability

* Actuator endpoints
* Tracing with `X-Trace-Id`
* Logging context via MDC
