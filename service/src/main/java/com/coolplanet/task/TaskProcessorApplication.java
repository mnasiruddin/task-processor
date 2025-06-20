
package com.coolplanet.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the TaskProcessor application.
 *
 * This class initializes and runs the Spring Boot application. It is
 * annotated with @SpringBootApplication, which is a convenience annotation
 * that combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.
 * The annotation facilitates component scanning and automatic configuration of the application.
 *
 * Additionally, the class uses @OpenAPIDefinition for OpenAPI 3.0 integration,
 * making the application capable of generating OpenAPI documentation for
 * RESTful APIs.
 */
@SpringBootApplication
public class TaskProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskProcessorApplication.class, args);
    }
}
