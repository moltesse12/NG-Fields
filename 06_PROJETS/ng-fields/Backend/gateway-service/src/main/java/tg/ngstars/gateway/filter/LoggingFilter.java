package tg.ngstars.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
        "Authorization", "Cookie", "Set-Cookie", "X-Auth-Token"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String routeId = exchange.getAttribute("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRouteId");
        String correlationId = exchange.getRequest().getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        long startTime = System.currentTimeMillis();

        log.info("[GATEWAY] >>> {} {} from {} | route={} | correlationId={}",
            request.getMethod(),
            request.getURI().getRawPath(),
            request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown",
            routeId != null ? routeId : "unknown",
            correlationId != null ? correlationId : "none");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            ServerHttpResponse response = exchange.getResponse();
            int statusCode = response.getStatusCode() != null ? response.getStatusCode().value() : 0;

            log.info("[GATEWAY] <<< {} {} | status={} | duration={}ms | route={}",
                request.getMethod(),
                request.getURI().getRawPath(),
                statusCode,
                duration,
                routeId != null ? routeId : "unknown");

            if (duration > 3000) {
                log.warn("[GATEWAY] SLOW REQUEST: {} {} took {}ms", request.getMethod(), request.getURI().getRawPath(), duration);
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}
