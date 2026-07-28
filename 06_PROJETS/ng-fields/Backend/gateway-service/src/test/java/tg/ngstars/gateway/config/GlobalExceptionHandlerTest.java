package tg.ngstars.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        handler = new GlobalExceptionHandler(objectMapper);
    }

    private ServerWebExchange createExchange() {
        var request = MockServerHttpRequest.get("/api/test").build();
        return MockServerWebExchange.from(request);
    }

    private ServerWebExchange createExchangeWithRoute(String routeId) {
        var exchange = createExchange();
        var route = mock(Route.class);
        when(route.getId()).thenReturn(routeId);
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, route);
        return exchange;
    }

    @Nested
    @DisplayName("CallNotPermittedException (circuit breaker ouvert)")
    class CircuitBreaker {

        @Test
        @DisplayName("Retourne 503 Service Unavailable")
        void retourne503() {
            var exchange = createExchangeWithRoute("auth-route");
            var ex = mock(CallNotPermittedException.class);

            StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
            assertEquals(MediaType.APPLICATION_PROBLEM_JSON, exchange.getResponse().getHeaders().getContentType());
        }

        @Test
        @DisplayName("Utilise 'unknown' si pas de route")
        void utiliseUnknownSiPasRoute() {
            var exchange = createExchange();
            var ex = mock(CallNotPermittedException.class);

            StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
        }
    }

    @Nested
    @DisplayName("ResponseStatusException")
    class ResponseStatus {

        @Test
        @DisplayName("Retourne le statut correspondant")
        void retourneStatutCorrespondant() {
            var exchange = createExchangeWithRoute("intervention-route");
            var ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Pas trouve");

            StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

            assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
        }
    }

    @Nested
    @DisplayName("Timeout")
    class Timeout {

        @Test
        @DisplayName("Retourne 504 Gateway Timeout pour messages contenant 'timeout'")
        void retourne504() {
            var exchange = createExchangeWithRoute("media-route");
            var ex = new RuntimeException("Read timeout executing GET");

            StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

            assertEquals(HttpStatus.GATEWAY_TIMEOUT, exchange.getResponse().getStatusCode());
        }
    }

    @Nested
    @DisplayName("Exception generique")
    class ExceptionGenerique {

        @Test
        @DisplayName("Retourne 500 Internal Server Error")
        void retourne500() {
            var exchange = createExchangeWithRoute("report-route");
            var ex = new RuntimeException("Erreur inattendue");

            StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
        }
    }
}
