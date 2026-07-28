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
public class TimeoutFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(TimeoutFilter.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();

        return chain.filter(exchange)
            .doOnSuccess(v -> logResponseTime(exchange, startTime))
            .doOnError(ex -> logResponseTime(exchange, startTime));
    }

    private void logResponseTime(ServerWebExchange exchange, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "unknown";

        if (durationMs > TIMEOUT.toMillis()) {
            log.warn("[TIMEOUT] Slow request from {} route={} duration={}ms",
                exchange.getRequest().getRemoteAddress(), routeId, durationMs);
        } else {
            log.debug("[TIMEOUT] Request processed in {}ms for route={}",
                durationMs, routeId);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
