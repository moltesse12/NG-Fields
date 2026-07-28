package tg.ngstars.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CorrelationIdFilter")
class CorrelationIdFilterTest {

    private CorrelationIdFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CorrelationIdFilter();
        MDC.clear();
    }

    @Nested
    @DisplayName("filter(exchange, chain)")
    class Filter {

        @Test
        @DisplayName("Place un UUID genere dans le MDC si aucun header X-Correlation-ID")
        void genereUuidSiPasHeader() {
            var request = MockServerHttpRequest.get("/api/test").build();
            var exchange = MockServerWebExchange.from(request);

            StepVerifier.create(filter.filter(exchange, chain -> {
                var mdcValue = MDC.get(CorrelationIdFilter.MDC_KEY);
                assertNotNull(mdcValue);
                assertDoesNotThrow(() -> UUID.fromString(mdcValue));
                return Mono.empty();
            })).verifyComplete();
        }

        @Test
        @DisplayName("Conserve le header X-Correlation-ID existant dans le MDC")
        void conserveHeaderExistant() {
            var existingId = "my-custom-id-123";
            var request = MockServerHttpRequest.get("/api/test")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, existingId)
                .build();
            var exchange = MockServerWebExchange.from(request);

            StepVerifier.create(filter.filter(exchange, chain -> {
                var mdcValue = MDC.get(CorrelationIdFilter.MDC_KEY);
                assertEquals(existingId, mdcValue);
                return Mono.empty();
            })).verifyComplete();
        }

        @Test
        @DisplayName("Regenere un UUID si le header est vide")
        void regenereUuidSiHeaderVide() {
            var request = MockServerHttpRequest.get("/api/test")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, "   ")
                .build();
            var exchange = MockServerWebExchange.from(request);

            StepVerifier.create(filter.filter(exchange, chain -> {
                var mdcValue = MDC.get(CorrelationIdFilter.MDC_KEY);
                assertNotNull(mdcValue);
                assertDoesNotThrow(() -> UUID.fromString(mdcValue));
                return Mono.empty();
            })).verifyComplete();
        }

        @Test
        @DisplayName("Regenere un UUID si le header depasse 128 caracteres")
        void regenereUuidSiTropLong() {
            var longId = "a".repeat(129);
            var request = MockServerHttpRequest.get("/api/test")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, longId)
                .build();
            var exchange = MockServerWebExchange.from(request);

            StepVerifier.create(filter.filter(exchange, chain -> {
                var mdcValue = MDC.get(CorrelationIdFilter.MDC_KEY);
                assertNotNull(mdcValue);
                assertNotEquals(longId, mdcValue);
                assertDoesNotThrow(() -> UUID.fromString(mdcValue));
                return Mono.empty();
            })).verifyComplete();
        }

        @Test
        @DisplayName("Nettoie le MDC apres le chain filter")
        void nettoieMDC() {
            var request = MockServerHttpRequest.get("/api/test").build();
            var exchange = MockServerWebExchange.from(request);

            StepVerifier.create(filter.filter(exchange, chain -> Mono.empty()))
                .verifyComplete();

            assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
        }
    }

    @Nested
    @DisplayName("getOrder()")
    class GetOrder {

        @Test
        @DisplayName("Retourne HIGHEST_PRECEDENCE + 1")
        void retourneHIGHEST_PRECEDENCEPlus1() {
            assertEquals(Integer.MIN_VALUE + 1, filter.getOrder());
        }
    }
}
