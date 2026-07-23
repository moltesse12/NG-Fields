package tg.ngstars.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gateway filter to add route ID headers to HTTP responses for request tracing.
 * This provides visibility into which gateway routes are processing requests for logging and monitoring purposes.
 */
public class RateLimitHeadersFilter implements GatewayFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitHeadersFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> addRouteHeaders(exchange)));
    }

    private void addRouteHeaders(ServerWebExchange exchange) {
        try {
            String routeId = getRouteId(exchange);
            ServerHttpResponse response = exchange.getResponse();

            if (routeId != null && !"unknown".equals(routeId)) {
                response.getHeaders().add("X-Route-ID", routeId);
                response.getHeaders().add("X-Route-ID-Date", java.time.Instant.now().toString());
            }
        } catch (Exception e) {
            log.debug("Failed to add route headers: {}", e.getMessage());
        }
    }

    private String getRouteId(ServerWebExchange exchange) {
        Object routeIdAttr = exchange.getAttribute("routeId");
        return routeIdAttr != null ? routeIdAttr.toString() : "unknown";
    }
}
