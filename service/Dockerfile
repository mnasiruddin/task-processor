
# Stage 1: Build the JAR using Maven
FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /app
COPY ../.. .
RUN mvn -pl service -am clean package -DskipTests

# Stage 2: Run the app
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/service/target/task-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
