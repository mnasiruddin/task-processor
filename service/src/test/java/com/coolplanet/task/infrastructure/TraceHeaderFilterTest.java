package com.coolplanet.task.infrastructure;

import com.coolplanet.task.config.TraceHeaderFilter;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

/**
 * Test class for verifying the behavior of {@link TraceHeaderFilter}.
 * This class ensures that the TraceHeaderFilter correctly manages trace
 * information in an incoming HTTP request and interacts appropriately
 * with the provided {@link Tracer} to create and manage spans.
 *
 * The test covers the following scenarios:
 *
 * 1. Adding an external trace ID:
 *    Verifies that when an "X-Trace-Id" header is present in the request,
 *    the filter tags the span with "external.traceId" using the value of this header.
 *
 * 2. Handling missing external trace ID:
 *    Confirms that if the "X-Trace-Id" header is absent, no external trace ID
 *    is tagged on the span.
 *
 * 3. Ensuring the filter completes the web filter chain:
 *    Validates that the filter completes processing and calls the
 *    {@link WebFilterChain#filter(ServerWebExchange)} method to propagate the request further.
 *
 * Dependencies and mocks:
 * - {@link Tracer}: Mocked to simulate span creation and interaction.
 * - {@link Span}: Mocked to verify interaction and tag application.
 * - {@link WebFilterChain}: Mocked to simulate continuation of the request handling pipeline.
 * - {@link StepVerifier}: Utilized for verifying the completion behavior of the filter.
 *
 * Primary validations include:
 * - The presence or absence of the "X-Trace-Id" header influences span tagging.
 * - Spans are appropriately ended after request processing.
 * - The filter correctly propagates the request through the web filter chain.
 */
class TraceHeaderFilterTest {

    @Autowired
    private Tracer tracer;

    @Test
    void shouldTagSpanWithExternalTraceId_WhenXTraceIdHeaderIsPresent() {
        Tracer tracerMock = mock(Tracer.class);
        Span spanMock = mock(Span.class);

        when(tracerMock.nextSpan()).thenReturn(spanMock);
        when(spanMock.name(anyString())).thenReturn(spanMock);

        TraceHeaderFilter filter = new TraceHeaderFilter(tracerMock);

        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header("X-Trace-Id", "12345")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chainMock = mock(WebFilterChain.class);
        when(chainMock.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chainMock))
                .expectComplete()
                .verify();

        verify(spanMock).tag("external.traceId", "12345");
        verify(spanMock).name("incoming-request");
        verify(spanMock).end();
    }

    @Test
    void shouldNotTagSpan_WhenXTraceIdHeaderIsAbsent() {
        Tracer tracerMock = mock(Tracer.class);
        Span spanMock = mock(Span.class);

        when(tracerMock.nextSpan()).thenReturn(spanMock);
        when(spanMock.name(anyString())).thenReturn(spanMock);

        TraceHeaderFilter filter = new TraceHeaderFilter(tracerMock);

        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chainMock = mock(WebFilterChain.class);
        when(chainMock.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chainMock))
                .expectComplete()
                .verify();

        verify(spanMock, never()).tag(eq("external.traceId"), anyString());
        verify(spanMock).name("incoming-request");
        verify(spanMock).end();
    }

    @Test
    void shouldCompleteWebFilterChain() {
        Tracer tracerMock = mock(Tracer.class);
        Span spanMock = mock(Span.class);

        when(tracerMock.nextSpan()).thenReturn(spanMock);
        when(spanMock.name(anyString())).thenReturn(spanMock);

        TraceHeaderFilter filter = new TraceHeaderFilter(tracerMock);

        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header("X-Trace-Id", "12345")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chainMock = mock(WebFilterChain.class);
        when(chainMock.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chainMock))
                .expectComplete()
                .verify();

        verify(chainMock).filter(exchange);
        verify(spanMock).end();
    }
}