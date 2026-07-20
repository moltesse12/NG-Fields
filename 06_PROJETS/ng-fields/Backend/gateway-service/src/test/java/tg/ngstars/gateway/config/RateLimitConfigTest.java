package tg.ngstars.gateway.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpRequest;

import reactor.test.StepVerifier;

class RateLimitConfigTest {

    private RateLimitConfig config;
    private ServerWebExchange exchange;
    private ServerHttpRequest request;

    @BeforeEach
    void setUp() {
        config = new RateLimitConfig();
        exchange = mock(ServerWebExchange.class);
        request = mock(ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
    }

    @Test
    void userKeyResolver_withPrincipal_shouldReturnUsername() {
        var principal = mock(Principal.class);
        when(principal.getName()).thenReturn("john.doe");
        when(exchange.getPrincipal()).thenReturn(reactor.core.publisher.Mono.just(principal));

        var resolver = config.userKeyResolver();

        StepVerifier.create(resolver.resolve(exchange))
            .assertNext(key -> assertEquals("john.doe", key))
            .verifyComplete();
    }

    @Test
    void userKeyResolver_withNoPrincipal_shouldReturnRemoteAddress() {
        when(exchange.getPrincipal()).thenReturn(reactor.core.publisher.Mono.empty());

        var address = new InetSocketAddress(InetAddress.getLoopbackAddress(), 8080);
        when(request.getRemoteAddress()).thenReturn(address);

        var resolver = config.userKeyResolver();

        StepVerifier.create(resolver.resolve(exchange))
            .assertNext(key -> assertEquals("127.0.0.1", key))
            .verifyComplete();
    }

    @Test
    void remoteAddrKeyResolver_withAddress_shouldReturnAddress() {
        var address = new InetSocketAddress(InetAddress.getLoopbackAddress(), 8080);
        when(request.getRemoteAddress()).thenReturn(address);

        var resolver = config.remoteAddrKeyResolver();

        StepVerifier.create(resolver.resolve(exchange))
            .assertNext(key -> assertEquals("127.0.0.1", key))
            .verifyComplete();
    }

    @Test
    void remoteAddrKeyResolver_withNoAddress_shouldReturnUnknown() {
        when(request.getRemoteAddress()).thenReturn(null);

        var resolver = config.remoteAddrKeyResolver();

        StepVerifier.create(resolver.resolve(exchange))
            .assertNext(key -> assertEquals("unknown", key))
            .verifyComplete();
    }
}
