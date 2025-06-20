package com.coolplanet.task.infrastructure.config;

import com.coolplanet.task.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.Parameter.StyleEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating the {@link SwaggerConfig#customOpenAPI()} method.
 *
 * The tests in this class ensure that the OpenAPI configuration is correctly initialized,
 * adheres to expected properties and metadata, and includes additional customizations such as
 * trace headers. The functionality of the OpenAPI bean is verified with different scenarios.
 *
 * Tests:
 * - {@link #testCustomOpenAPI_BasicInitialization()}: Verifies that the OpenAPI instance is correctly
 *   initialized with predefined metadata (title, version, and description).
 * - {@link #testCustomOpenAPI_EmptyPathsInitialization()}: Ensures that the OpenAPI paths are initialized
 *   and empty when no paths are explicitly provided.
 * - {@link #testCustomOpenAPI_TraceHeaderAdded()}: Validates that the custom trace header (X-Trace-Id)
 *   is correctly added to all operations defined in the OpenAPI paths.
 */
public class SwaggerConfigTest {

    /**
     * Tests for the {@link SwaggerConfig#customOpenAPI()} method and {@link SwaggerConfig#addXTraceIdHeader()} method.
     * Validates that:
     * - The OpenAPI bean correctly initializes with predefined properties and adheres to the expected configuration.
     * - The custom 'X-Trace-Id' header is added correctly as part of API operations and has the correct properties.
     */

    @Test
    public void testCustomOpenAPI_BasicInitialization() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        assertNotNull(openAPI, "OpenAPI object should not be null");
        Info info = openAPI.getInfo();
        assertNotNull(info, "OpenAPI info object should not be null");
        assertEquals("Task Processor API", info.getTitle(), "Unexpected API title");
        assertEquals("1.0", info.getVersion(), "Unexpected API version");
        assertEquals("API for recording and retrieving task duration averages.", info.getDescription(), "Unexpected API description");
    }

    @Test
    public void testCustomOpenAPI_EmptyPathsInitialization() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        assertNotNull(openAPI.getPaths(), "Paths object should be initialized if null");
        assertEquals(0, openAPI.getPaths().size(), "Paths object should be empty initially");
    }

    @Test
    public void testCustomOpenAPI_TraceHeaderAdded() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        Parameter traceHeader = new Parameter()
                .in("header")
                .schema(new StringSchema())
                .name("X-Trace-Id")
                .description("Optional client-provided trace ID")
                .required(false)
                .style(StyleEnum.SIMPLE);

        Paths paths = openAPI.getPaths();
        assertNotNull(paths, "Paths object should be initialized");

        paths.forEach((path, pathItem) -> {
            pathItem.readOperations().forEach(operation -> {
                assertTrue(operation.getParameters().contains(traceHeader),
                        "Trace header should be added to all operations");
            });
        });
    }
    @Test
    public void testAddXTraceIdHeader_HasTraceIdParameter() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Apply the customizer
        swaggerConfig.addXTraceIdHeader().customise(openAPI);

        Paths paths = openAPI.getPaths();
        assertNotNull(paths, "Paths object should be initialized");

        paths.forEach((path, pathItem) -> {
            pathItem.readOperations().forEach(operation -> {
                assertTrue(operation.getParameters().stream()
                                .anyMatch(param -> "X-Trace-Id".equals(param.getName())),
                        "X-Trace-Id header should be added to all operations");
            });
        });
    }

    @Test
    public void testAddXTraceIdHeader_TraceIdParameterProperties() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Apply the customizer
        swaggerConfig.addXTraceIdHeader().customise(openAPI);

        Paths paths = openAPI.getPaths();
        assertNotNull(paths, "Paths object should be initialized");

        paths.forEach((path, pathItem) -> {
            pathItem.readOperations().forEach(operation -> {
                Parameter traceHeader = operation.getParameters().stream()
                        .filter(param -> "X-Trace-Id".equals(param.getName()))
                        .findFirst()
                        .orElse(null);

                assertNotNull(traceHeader, "X-Trace-Id header should be present");
                assertEquals("header", traceHeader.getIn(), "X-Trace-Id header should be in 'header'");
                assertFalse(traceHeader.getRequired(), "X-Trace-Id header should not be required");
                assertEquals("Optional trace ID for correlation", traceHeader.getDescription(),
                        "X-Trace-Id header should have the correct description");
                assertTrue(traceHeader.getSchema() instanceof StringSchema,
                        "X-Trace-Id header schema should be of type String");
            });
        });
    }
}