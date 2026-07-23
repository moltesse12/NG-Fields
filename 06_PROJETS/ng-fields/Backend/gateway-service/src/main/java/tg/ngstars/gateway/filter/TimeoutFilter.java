package tg.ngstars.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Gateway filter that tracks request timeouts and response headers.
 * This filter provides enhanced timeout tracking and diagnostic information for slow or hanging requests.
 */
public class TimeoutFilter implements GatewayFilter {

    private static final Logger log = LoggerFactory.getLogger(TimeoutFilter.class);
    private final Duration timeout = Duration.ofSeconds(30);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();
        
        return chain.filter(exchange)
            .doOnSuccess(v -> logResponseTime(exchange, startTime))
            .doOnError(ex -> logResponseTime(exchange, startTime));
    }

    private void logResponseTime(ServerWebExchange exchange, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        
        String routeId = exchange.getAttribute("routeId");
        if (routeId == null) {
            routeId = "unknown";
        }
        
        if (durationMs > timeout.toMillis()) {
            log.warn("[TIMEOUT] Slow request from {} route={} duration={}ms", 
                exchange.getRequest().getRemoteAddress(), routeId, durationMs);
        } else {
            log.debug("[TIMEOUT] Request processed in {}ms for route={}", 
                durationMs, routeId);
        }
    }
}
