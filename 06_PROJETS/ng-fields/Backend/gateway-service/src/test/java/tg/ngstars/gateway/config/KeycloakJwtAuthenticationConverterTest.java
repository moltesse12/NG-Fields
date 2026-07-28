package tg.ngstars.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("KeycloakJwtAuthenticationConverter")
class KeycloakJwtAuthenticationConverterTest {

    private KeycloakJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakJwtAuthenticationConverter();
    }

    @Nested
    @DisplayName("convert(jwt)")
    class Convert {

        @Test
        @DisplayName("Extrait les roles et retourne un JwtAuthenticationToken")
        void extraitRoles() {
            var jwt = mock(Jwt.class);
            when(jwt.getSubject()).thenReturn("user-123");
            when(jwt.getClaim("realm_access")).thenReturn(Map.of(
                "roles", List.of("ADMIN", "TECHNICIAN")
            ));

            StepVerifier.create(converter.convert(jwt))
                .assertNext(auth -> {
                    assertTrue(auth instanceof JwtAuthenticationToken);
                    var jwtAuth = (JwtAuthenticationToken) auth;
                    assertEquals("user-123", jwtAuth.getName());
                    var authorities = jwtAuth.getAuthorities().stream()
                        .map(a -> a.getAuthority()).toList();
                    assertEquals(2, authorities.size());
                    assertTrue(authorities.contains("ROLE_ADMIN"));
                    assertTrue(authorities.contains("ROLE_TECHNICIAN"));
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Retourne liste vide si realm_access absent")
        void retourneListeVideSiRealmAccessAbsent() {
            var jwt = mock(Jwt.class);
            when(jwt.getSubject()).thenReturn("user-123");
            when(jwt.getClaim("realm_access")).thenReturn(null);

            StepVerifier.create(converter.convert(jwt))
                .assertNext(auth -> {
                    var jwtAuth = (JwtAuthenticationToken) auth;
                    assertTrue(jwtAuth.getAuthorities().isEmpty());
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Convertit les roles en majuscules")
        void convertitEnMajuscules() {
            var jwt = mock(Jwt.class);
            when(jwt.getSubject()).thenReturn("user-123");
            when(jwt.getClaim("realm_access")).thenReturn(Map.of(
                "roles", List.of("technician")
            ));

            StepVerifier.create(converter.convert(jwt))
                .assertNext(auth -> {
                    var authorities = auth.getAuthorities().stream()
                        .map(a -> a.getAuthority()).toList();
                    assertTrue(authorities.contains("ROLE_TECHNICIAN"));
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Gere les elements non-String dans la liste des roles")
        void gereElementsNonString() {
            var jwt = mock(Jwt.class);
            when(jwt.getSubject()).thenReturn("user-123");
            when(jwt.getClaim("realm_access")).thenReturn(Map.of(
                "roles", List.of("ADMIN", 123, "USER")
            ));

            StepVerifier.create(converter.convert(jwt))
                .assertNext(auth -> {
                    var authorities = auth.getAuthorities().stream()
                        .map(a -> a.getAuthority()).toList();
                    assertEquals(2, authorities.size());
                    assertTrue(authorities.contains("ROLE_ADMIN"));
                    assertTrue(authorities.contains("ROLE_USER"));
                })
                .verifyComplete();
        }
    }
}
