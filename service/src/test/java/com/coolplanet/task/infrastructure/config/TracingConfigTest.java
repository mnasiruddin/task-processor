package com.coolplanet.task.infrastructure.config;

import com.coolplanet.task.config.TracingConfig;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for validating the functionality and configuration of {@link TracingConfig}.
 *
 * This class uses Spring's testing framework to load the application context
 * and verify the beans defined in {@link TracingConfig}. Tests ensure that
 * the distributed tracing setup is correctly initialized and that the appropriate
 * implementation of {@link Tracer} is used in the application.
 *
 * Annotations:
 * - {@link SpringBootTest}: Specifies that the test should be executed in the context of
 *   a Spring Boot application with {@link TracingConfig} as the configuration class.
 *
 * Fields:
 * - {@code tracingConfig}: An instance of {@link TracingConfig}, autowired by Spring's
 *   dependency injection mechanism. Used to verify the defined beans in the configuration.
 *
 * Tests:
 * - {@link #testTracerBeanIsCreated()}: Ensures that the {@link Tracer} bean
 *   is created, loaded into the application context, and is not null.
 * - {@link #testTracerBeanIsOfCorrectType()}: Verifies that the bean created
 *   is an instance of {@link BraveTracer}, validating the integration with Brave.
 */
@SpringBootTest(classes = TracingConfig.class)
public class TracingConfigTest {

    @Autowired
    private TracingConfig tracingConfig;

    /**
     * Tests that the tracer bean is created and is not null.
     */
    @Test
    public void testTracerBeanIsCreated() {
        Tracer tracer = tracingConfig.tracer();

        assertNotNull(tracer, "Tracer should not be null");
    }

    /**
     * Verifies that the BraveTracer implementation is injected.
     */
    @Test
    public void testTracerBeanIsOfCorrectType() {
        Tracer tracer = tracingConfig.tracer();

        assertNotNull(tracer, "Tracer should not be null");
        assert (tracer instanceof BraveTracer);
    }

}