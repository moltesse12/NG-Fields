package tg.ngstars.gateway.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;

@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.rebuild()
            .changeDefaultPropertyInclusion(v -> v.withValueInclusion(JsonInclude.Include.NON_NULL))
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        String routeId = getRouteId(exchange);
        ProblemDetail problem = buildProblem(ex, routeId);

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(problem.getStatus()));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(problem);
            var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        } catch (Exception e) {
            log.error("Failed to serialize ProblemDetail", e);
            return Mono.error(ex);
        }
    }

    private String getRouteId(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        return route != null ? route.getId() : "unknown";
    }

    private ProblemDetail buildProblem(Throwable ex, String routeId) {
        if (ex instanceof CallNotPermittedException) {
            log.warn("[GATEWAY] Circuit breaker OPEN for route={}", routeId);
            return buildProblem(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable",
                "Circuit breaker is open for " + routeId + ". Service is temporarily unavailable.", routeId);
        }

        if (ex instanceof ResponseStatusException rse) {
            HttpStatus status = HttpStatus.resolve(rse.getStatusCode().value());
            if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
            return buildProblem(status, status.getReasonPhrase(), ex.getMessage(), routeId);
        }

        if (ex.getMessage() != null && ex.getMessage().contains("timeout")) {
            log.warn("[GATEWAY] Request timeout for route={}: {}", routeId, ex.getMessage());
            return buildProblem(HttpStatus.GATEWAY_TIMEOUT, "Gateway Timeout",
                "Service " + routeId + " did not respond in time.", routeId);
        }

        log.error("[GATEWAY] Unhandled exception on route={}", routeId, ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
            "An unexpected error occurred", routeId);
    }

    private ProblemDetail buildProblem(HttpStatus status, String title, String detail, String routeId) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setType(URI.create("about:blank"));
        pd.setProperty("routeId", routeId);
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }
}
