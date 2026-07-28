package tg.ngstars.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
public class MetricsFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(MetricsFilter.class);
    private static final int PERFORMANCE_THRESHOLD_MS = 1000;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "unknown";

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

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }
}
