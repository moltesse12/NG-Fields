package tg.ngstars.common.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SecurityUtils")
class SecurityUtilsTest {

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("getCurrentUserId()")
    class GetCurrentUserId {

        @Test
        @DisplayName("Retourne l'UUID du sub du JWT")
        void retourneUuidDuSub() {
            var userId = UUID.randomUUID();
            var jwt = mock(Jwt.class);
            when(jwt.getClaimAsString("sub")).thenReturn(userId.toString());
            var token = new JwtAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(token);

            assertEquals(userId, SecurityUtils.getCurrentUserId());
        }

        @Test
        @DisplayName("Lance exception si pas d'authentification JWT")
        void lanceExceptionSiPasJWT() {
            assertThrows(IllegalStateException.class, SecurityUtils::getCurrentUserId);
        }

        @Test
        @DisplayName("Lance exception si sub est null")
        void lanceExceptionSiSubNull() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaimAsString("sub")).thenReturn(null);
            var token = new JwtAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(token);

            assertThrows(IllegalStateException.class, SecurityUtils::getCurrentUserId);
        }
    }

    @Nested
    @DisplayName("getCurrentUsername()")
    class GetCurrentUsername {

        @Test
        @DisplayName("Retourne le preferred_username du JWT")
        void retournePreferredUsername() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaimAsString("preferred_username")).thenReturn("john.doe");
            var token = new JwtAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(token);

            assertEquals("john.doe", SecurityUtils.getCurrentUsername());
        }

        @Test
        @DisplayName("Lance exception si pas d'authentification JWT")
        void lanceExceptionSiPasJWT() {
            assertThrows(IllegalStateException.class, SecurityUtils::getCurrentUsername);
        }

        @Test
        @DisplayName("Lance exception si preferred_username est null")
        void lanceExceptionSiPreferredUsernameNull() {
            var jwt = mock(Jwt.class);
            when(jwt.getClaimAsString("preferred_username")).thenReturn(null);
            var token = new JwtAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(token);

            assertThrows(IllegalStateException.class, SecurityUtils::getCurrentUsername);
        }
    }

    @Nested
    @DisplayName("hasRole(role)")
    class HasRole {

        @Test
        @DisplayName("Retourne true si le role est present")
        void retourneTrueSiRolePresent() {
            var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
            );
            var jwt = mock(Jwt.class);
            var token = new JwtAuthenticationToken(jwt, authorities, "user");
            SecurityContextHolder.getContext().setAuthentication(token);

            assertTrue(SecurityUtils.hasRole("ADMIN"));
        }

        @Test
        @DisplayName("Retourne false si le role est absent")
        void retourneFalseSiRoleAbsent() {
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            var jwt = mock(Jwt.class);
            var token = new JwtAuthenticationToken(jwt, authorities, "user");
            SecurityContextHolder.getContext().setAuthentication(token);

            assertFalse(SecurityUtils.hasRole("ADMIN"));
        }

        @Test
        @DisplayName("Ignore la casse du role")
        void ignoreCasseDuRole() {
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            var jwt = mock(Jwt.class);
            var token = new JwtAuthenticationToken(jwt, authorities, "user");
            SecurityContextHolder.getContext().setAuthentication(token);

            assertTrue(SecurityUtils.hasRole("admin"));
            assertTrue(SecurityUtils.hasRole("Admin"));
        }

        @Test
        @DisplayName("Retourne false si pas d'authentification")
        void retourneFalseSiPasAuth() {
            assertFalse(SecurityUtils.hasRole("ADMIN"));
        }
    }

    @Nested
    @DisplayName("isAdminOrManager()")
    class IsAdminOrManager {

        @Test
        @DisplayName("Retourne true pour ADMIN")
        void retourneTruePourAdmin() {
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            var jwt = mock(Jwt.class);
            var token = new JwtAuthenticationToken(jwt, authorities, "user");
            SecurityContextHolder.getContext().setAuthentication(token);

            assertTrue(SecurityUtils.isAdminOrManager());
        }

        @Test
        @DisplayName("Retourne true pour MANAGER")
        void retourneTruePourManager() {
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_MANAGER"));
            var jwt = mock(Jwt.class);
            var token = new JwtAuthenticationToken(jwt, authorities, "user");
            SecurityContextHolder.getContext().setAuthentication(token);

            assertTrue(SecurityUtils.isAdminOrManager());
        }

        @Test
        @DisplayName("Retourne false pour TECHNICIAN")
        void retourneFalsePourTechnician() {
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_TECHNICIAN"));
            var jwt = mock(Jwt.class);
            var token = new JwtAuthenticationToken(jwt, authorities, "user");
            SecurityContextHolder.getContext().setAuthentication(token);

            assertFalse(SecurityUtils.isAdminOrManager());
        }

        @Test
        @DisplayName("Retourne false si pas d'authentification")
        void retourneFalseSiPasAuth() {
            assertFalse(SecurityUtils.isAdminOrManager());
        }
    }
}
