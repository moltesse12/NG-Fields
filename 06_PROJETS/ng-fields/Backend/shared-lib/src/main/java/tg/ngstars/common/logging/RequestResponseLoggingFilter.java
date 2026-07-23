package tg.ngstars.common.logging;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_BODY_LOG_SIZE = 4096;
    private static final int REQUEST_CACHE_LIMIT = 10240;
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization", "cookie", "set-cookie", "x-auth-token", "x-api-key");
    private static final Set<String> SENSITIVE_BODY_FIELDS = Set.of(
            "password", "secret", "token", "accessToken", "refreshToken",
            "client_secret", "admin-client-secret", "authorization");
    private static final Set<String> SKIP_PATHS = Set.of(
            "/actuator", "/health", "/swagger-ui", "/v3/api-docs");

    @Value("${logging.request-response.enabled:false}")
    private boolean enabled;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!enabled) return true;
        var path = request.getRequestURI();
        return SKIP_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var wrappedRequest = new ContentCachingRequestWrapper(request, REQUEST_CACHE_LIMIT);
        var wrappedResponse = new ContentCachingResponseWrapper(response);

        logRequest(wrappedRequest);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logResponse(wrappedRequest, wrappedResponse, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        if (!log.isDebugEnabled()) return;

        var method = request.getMethod();
        var uri = request.getRequestURI();
        var queryString = request.getQueryString();
        var remoteAddr = request.getRemoteAddr();

        log.debug("→ {} {}{} from {}", method, uri,
                queryString != null ? "?" + queryString : "", remoteAddr);

        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            var name = headerNames.nextElement();
            if (SENSITIVE_HEADERS.contains(name.toLowerCase())) {
                log.debug("  Header: {}=***", name);
            } else {
                log.debug("  Header: {}={}", name, request.getHeader(name));
            }
        }

        var body = getBody(request.getContentAsByteArray(), request.getContentType());
        if (!body.isEmpty()) {
            log.debug("  Body: {}", body);
        }
    }

    private void logResponse(ContentCachingRequestWrapper request,
                             ContentCachingResponseWrapper response, long duration) {
        if (!log.isDebugEnabled()) return;

        var method = request.getMethod();
        var uri = request.getRequestURI();
        var status = response.getStatus();

        log.debug("← {} {} → {} ({}ms)", method, uri, status, duration);

        var body = getBody(response.getContentAsByteArray(), response.getContentType());
        if (!body.isEmpty()) {
            log.debug("  Response body: {}", body);
        }
    }

    private String getBody(byte[] content, @Nullable String contentType) {
        if (content == null || content.length == 0) return "";
        if (contentType != null && !contentType.contains("json") && !contentType.contains("text")) return "";

        var body = new String(content, 0, Math.min(content.length, MAX_BODY_LOG_SIZE), StandardCharsets.UTF_8);
        return sanitizeBody(body);
    }

    private String sanitizeBody(String body) {
        if (body == null || body.isBlank()) return body;
        var sanitized = body;
        for (var field : SENSITIVE_BODY_FIELDS) {
            sanitized = sanitized.replaceAll(
                    "(?i)(\"" + field + "\"\\s*:\s*\")([^\"]{1,50})(\")",
                    "$1***$3");
        }
        return sanitized;
    }
}
