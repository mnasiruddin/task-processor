package com.coolplanet.task.config;

import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up distributed tracing capabilities in the application.
 *
 * This class integrates Brave with Micrometer for providing trace context propagation and management,
 * enabling observability of distributed systems by tracking requests across services.
 *
 * Annotations:
 * - {@link Configuration}: Marks this class as a configuration class in Spring's context.
 *
 * Beans:
 * - {@link #tracer()}: Configures a {@link Tracer} bean using Brave and its integration with Micrometer.
 *   It sets up context propagation using {@link ThreadLocalCurrentTraceContext} with an MDC decorator
 *   for attaching trace information to logs. The resulting configuration provides a bridge between
 *   Brave's tracing and Micrometer's tracing abstractions.
 */
@Configuration
public class TracingConfig {

    @Bean
    public Tracer tracer() {
        // Brave's current trace context with MDC for logs

        brave.propagation.CurrentTraceContext braveContext =
                ThreadLocalCurrentTraceContext.newBuilder()
                        .addScopeDecorator(MDCScopeDecorator.get())
                        .build();

        // Create the Brave Tracing instance
        Tracing braveTracing = Tracing.newBuilder()
                .currentTraceContext(braveContext)
                .build();

        // Wrap Brave's context into Micrometer's bridge
        return new BraveTracer(
                braveTracing.tracer(),
                new BraveCurrentTraceContext(braveContext),
                new BraveBaggageManager()
        );
    }
}
