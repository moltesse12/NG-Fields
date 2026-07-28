package tg.ngstars.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RealmRoleConverter")
class RealmRoleConverterTest {

    private RealmRoleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new RealmRoleConverter();
    }

    @Nested
    @DisplayName("convert(jwt)")
    class Convert {

        @Test
        @DisplayName("Extrait les roles realm_access et les prefixe par ROLE_")
        void extraitRolesAvecPrefixeROLE() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaims()).thenReturn(Map.of(
                "realm_access", Map.of("roles", List.of("ADMIN", "MANAGER"))
            ));

            var authorities = converter.convert(jwt);

            assertEquals(2, authorities.size());
            var names = authorities.stream().map(a -> a.getAuthority()).toList();
            assertTrue(names.contains("ROLE_ADMIN"));
            assertTrue(names.contains("ROLE_MANAGER"));
        }

        @Test
        @DisplayName("Convertit les roles en majuscules")
        void convertitEnMajuscules() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaims()).thenReturn(Map.of(
                "realm_access", Map.of("roles", List.of("technician"))
            ));

            var authorities = converter.convert(jwt);

            assertEquals(1, authorities.size());
            assertEquals("ROLE_TECHNICIAN", authorities.iterator().next().getAuthority());
        }

        @Test
        @DisplayName("Retourne liste vide si realm_access absent")
        void retourneListeVideSiRealmAccessAbsent() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaims()).thenReturn(Map.of());

            var authorities = converter.convert(jwt);

            assertTrue(authorities.isEmpty());
        }

        @Test
        @DisplayName("Retourne liste vide si roles n'est pas une liste")
        void retourneListeVideSiRolesPasListe() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaims()).thenReturn(Map.of(
                "realm_access", Map.of("roles", "ADMIN")
            ));

            var authorities = converter.convert(jwt);

            assertTrue(authorities.isEmpty());
        }

        @Test
        @DisplayName("Retourne liste vide si realm_access n'est pas une Map")
        void retourneListeVideSiRealmAccessPasMap() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaims()).thenReturn(Map.of(
                "realm_access", "not-a-map"
            ));

            var authorities = converter.convert(jwt);

            assertTrue(authorities.isEmpty());
        }

        @Test
        @DisplayName("Filtre les elements non-String dans la liste des roles")
        void filtreElementsNonString() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaims()).thenReturn(Map.of(
                "realm_access", Map.of("roles", List.of("ADMIN", 123, "USER"))
            ));

            var authorities = converter.convert(jwt);

            assertEquals(2, authorities.size());
            var names = authorities.stream().map(a -> a.getAuthority()).toList();
            assertTrue(names.contains("ROLE_ADMIN"));
            assertTrue(names.contains("ROLE_USER"));
        }

        @Test
        @DisplayName("Gere une liste de roles vide")
        void gereListeRolesVide() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaims()).thenReturn(Map.of(
                "realm_access", Map.of("roles", List.of())
            ));

            var authorities = converter.convert(jwt);

            assertTrue(authorities.isEmpty());
        }
    }
}
