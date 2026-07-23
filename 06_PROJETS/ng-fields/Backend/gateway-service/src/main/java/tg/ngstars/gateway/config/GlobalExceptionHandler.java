package tg.ngstars.gateway.config;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;

/**
 * Global exception handler for the API Gateway that provides RFC 7807 ProblemDetail responses.
 * This handles errors at the gateway level, including circuit breaker failures and timeouts.
 */
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        String routeId = getRouteId(exchange);

        ProblemDetail problem = buildProblem(ex, routeId);

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(problem.getStatus()));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        String json = toJson(problem);
        byte[] bytes = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        org.springframework.core.io.buffer.DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    private String getRouteId(ServerWebExchange exchange) {
        String routeId = exchange.getAttribute("routeId");
        if (routeId != null) {
            return routeId;
        }
        return "unknown";
    }

    private ProblemDetail buildProblem(Throwable ex, String routeId) {
        if (ex instanceof CallNotPermittedException) {
            log.warn("[GATEWAY] Circuit breaker OPEN for route={}", routeId);
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
            pd.setTitle("Service Unavailable");
            pd.setDetail("Circuit breaker is open for " + routeId + ". Service is temporarily unavailable.");
            pd.setType(URI.create("about:blank"));
            pd.setProperty("routeId", routeId);
            pd.setProperty("timestamp", Instant.now().toString());
            return pd;
        }

        if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
            org.springframework.http.HttpStatus status = org.springframework.http.HttpStatus.resolve(rse.getStatusCode().value());
            if (status == null) status = org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
            ProblemDetail pd = ProblemDetail.forStatus(status);
            pd.setTitle(status.getReasonPhrase());
            pd.setDetail(ex.getMessage());
            pd.setType(URI.create("about:blank"));
            pd.setProperty("routeId", routeId);
            pd.setProperty("timestamp", Instant.now().toString());
            return pd;
        }

        if (ex.getMessage() != null && ex.getMessage().contains("timeout")) {
            log.warn("[GATEWAY] Request timeout for route={}: {}", routeId, ex.getMessage());
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.GATEWAY_TIMEOUT);
            pd.setTitle("Gateway Timeout");
            pd.setDetail("Service " + routeId + " did not respond in time.");
            pd.setType(URI.create("about:blank"));
            pd.setProperty("routeId", routeId);
            pd.setProperty("timestamp", Instant.now().toString());
            return pd;
        }

        log.error("[GATEWAY] Unhandled exception on route={}", routeId, ex);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Internal Server Error");
        pd.setDetail("An unexpected error occurred");
        pd.setType(URI.create("about:blank"));
        pd.setProperty("routeId", routeId);
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }

    private String toJson(ProblemDetail pd) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"").append(escape(pd.getType() != null ? pd.getType().toString() : "about:blank")).append("\"");
        sb.append(",\"title\":\"").append(escape(pd.getTitle())).append("\"");
        sb.append(",\"status\":").append(pd.getStatus());
        sb.append(",\"detail\":\"").append(escape(pd.getDetail())).append("\"");
        if (pd.getProperties() != null && !pd.getProperties().isEmpty()) {
            pd.getProperties().forEach((k, v) ->
                sb.append(",\"").append(escape(k)).append("\":\"").append(escape(String.valueOf(v))).append("\"")
            );
        }
        sb.append("}");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}