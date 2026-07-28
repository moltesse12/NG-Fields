package tg.ngstars.gateway.config;

import tools.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tg.ngstars.gateway.dto.FallbackResponse;

@RestController
@Timed
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);
    private final ObjectMapper objectMapper;

    public FallbackController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RequestMapping("/fallback")
    public Mono<Void> fallback(ServerWebExchange exchange) {
        Object routeObj = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = (routeObj instanceof org.springframework.cloud.gateway.route.Route route)
                ? route.getId() : "unknown";
        if (routeId == null) routeId = "unknown";

        log.warn("[GATEWAY] Fallback triggered for route={}", routeId);

        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        try {
            FallbackResponse response = FallbackResponse.of(routeId,
                "Service " + routeId + " is temporarily unavailable. Please try again later.");
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Failed to serialize fallback response", e);
            return Mono.error(e);
        }
    }
}
