
package com.coolplanet.task.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Swagger/OpenAPI documentation for the Task Processor API.
 *
 * This class defines the OpenAPI configuration for generating API documentation
 * and includes setup for a bearer authentication scheme utilizing JWT tokens.
 *
 * Annotations:
 * - {@link Configuration}: Marks this class as a configuration class in Spring's context.
 *
 * Beans:
 * - {@link #customOpenAPI()}: Configures an {@link OpenAPI} instance with API metadata
 *   such as title, version, description, and security schemes. It defines a bearer authentication
 *   mechanism for securing API endpoints.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Task Processor API")
                        .version("1.0")
                        .description("API for recording and retrieving task duration averages."));

        // Initialize Paths if null
        if (openAPI.getPaths() == null) {
            openAPI.setPaths(new Paths());
        }

        Parameter traceHeader = new Parameter()
                .in(ParameterIn.HEADER.toString())
                .schema(new StringSchema())
                .name("X-Trace-Id")
                .description("Optional client-provided trace ID")
                .required(false);

        // Add the parameter to all paths when they are created
        openAPI.getPaths().forEach((path, pathItem) -> {
            if (pathItem != null) {
                pathItem.readOperations().forEach(operation -> {
                    operation.addParametersItem(traceHeader);
                });
            }
        });

        return openAPI;
    }

    @Bean
    public OpenApiCustomizer addXTraceIdHeader() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation ->
                        operation.addParametersItem(new Parameter()
                                .in(ParameterIn.HEADER.toString())
                                .name("X-Trace-Id")
                                .required(false)
                                .description("Optional trace ID for correlation")
                                .schema(new StringSchema()))
                )
        );
    }

}
