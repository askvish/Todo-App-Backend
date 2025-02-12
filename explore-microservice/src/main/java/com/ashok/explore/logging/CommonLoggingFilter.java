package com.ashok.explore.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class CommonLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonLoggingFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Generate a unique correlation ID for tracing
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId); // For SLF4J MDC

        long startTime = System.currentTimeMillis();

        // Wrap request/response to cache their content
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            // Log request details
            logRequest(requestWrapper);

            // Proceed with the filter chain
            filterChain.doFilter(requestWrapper, responseWrapper);

        } finally {
            // Log response details
            logResponse(responseWrapper, startTime);
            responseWrapper.copyBodyToResponse(); // Write cached response back to the client

            MDC.remove("correlationId"); // Clear MDC after processing
        }

    }

    private void logRequest(ContentCachingRequestWrapper request) {
        LOGGER.info(
                "Request | Method: {}, URI: {}, Headers: {}, Parameters: {}, Body: {}",
                request.getMethod(),
                request.getRequestURI(),
                getHeaders(request),
                request.getParameterMap(),
                getRequestBody(request)
        );
    }

    private void logResponse(ContentCachingResponseWrapper response, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info(
                "Response | Status: {}, Headers: {}, Body: {}, Duration: {} ms",
                response.getStatus(),
                getHeaders(response),
                getResponseBody(response),
                duration
        );
    }

    private String getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        return headers.toString();
    }

    private String getHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        for (String header : response.getHeaderNames()) {
            headers.put(header, response.getHeader(header));
        }
        return headers.toString();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        return content.length > 0 ? new String(content, StandardCharsets.UTF_8) : "";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        return content.length > 0 ? new String(content, StandardCharsets.UTF_8) : "";
    }
}
