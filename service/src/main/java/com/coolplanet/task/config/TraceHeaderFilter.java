package com.coolplanet.task.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * A Spring WebFlux {@code WebFilter} implementation that manages tracing headers for incoming requests. This filter
 * uses a {@link Tracer} instance to create a new span and optionally tags the span with an external trace ID if
 * provided in the request headers.
 *
 * Features:
 * - Extracts the "X-Trace-Id" header from the incoming HTTP request.
 * - Creates a new span associated with the incoming request.
 * - Optionally tags the span with the extracted trace ID.
 * - Closes the span after the request is processed.
 * - Propagates the span through the Reactor context for downstream processing.
 *
 * Constructor:
 * - {@code TraceHeaderFilter(Tracer tracer)}: Initializes the filter with a {@code Tracer} instance for handling spans.
 *
 * Methods:
 * - {@code filter(ServerWebExchange exchange, WebFilterChain chain)}: Processes an HTTP request by managing tracing
 *   spans and propagating the trace context through the Reactor execution model. The method creates a span, optionally
 *   tags it with the external trace ID, and ensures the span is closed when the request lifecycle completes.
 *
 * Usage Scenarios:
 * - Adding tracing capabilities to a Spring WebFlux-based application.
 * - Capturing and tagging spans for incoming HTTP requests.
 * - Propagating the trace context during reactive request handling.
 */
@Component
public class TraceHeaderFilter implements WebFilter {

    private final Tracer tracer;

    public TraceHeaderFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        String incomingTraceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");

        Span span = tracer.nextSpan().name("incoming-request");

        // Optionally, tag trace ID manually
        if (incomingTraceId != null) {
            span.tag("external.traceId", incomingTraceId);
        }

        return chain.filter(exchange)
                .doOnEach(signal -> span.end()) // close span
                .contextWrite(ctx -> ctx.put(Span.class, span)); // inject into Reactor context
    }
}


