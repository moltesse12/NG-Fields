package tg.ngstars.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Gateway filter to track request metrics and performance statistics.
 * This provides visibility into gateway performance and request processing for monitoring and analytics.
 */
public class MetricsFilter implements GatewayFilter {

    private static final Logger log = LoggerFactory.getLogger(MetricsFilter.class);
    private static final int PERFORMANCE_THRESHOLD_MS = 1000;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();
        String routeId = exchange.getAttribute("routeId");        
        if (routeId == null) {
            routeId = "unknown";
        }
        
        return chain.filter(exchange)
            .doOnSuccess(v -> logPerformanceStats(exchange, startTime, routeId))
            .doOnError(ex -> logPerformanceStats(exchange, startTime, routeId));
    }

    private void logPerformanceStats(ServerWebExchange exchange, Instant startTime, String routeId) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        
        if (durationMs > PERFORMANCE_THRESHOLD_MS) {
            log.warn("[METRICS] Slow {} request: route={} path={} duration={}ms", 
                method, routeId, path, durationMs);
        } else {
            log.debug("[METRICS] {} request: route={} path={} duration={}ms", 
                method, routeId, path, durationMs);
        }
    }
}
