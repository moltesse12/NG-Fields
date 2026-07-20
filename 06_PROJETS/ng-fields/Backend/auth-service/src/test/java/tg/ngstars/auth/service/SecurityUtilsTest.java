package tg.ngstars.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    SecurityUtils service = new SecurityUtils();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_withValidJwt_shouldReturnUuid() {
        var uuid = UUID.randomUUID();
        var jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(uuid.toString());

        var auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(jwt);

        SecurityContextHolder.getContext().setAuthentication(auth);

        var result = service.getCurrentUserId();
        assertTrue(result.isPresent());
        assertEquals(uuid, result.get());
    }

    @Test
    void getCurrentUserId_withNoAuth_shouldThrow() {
        SecurityContextHolder.clearContext();
        var result = service.getCurrentUserId();
        assertTrue(result.isEmpty());
    }

    @Test
    void getCurrentUserId_withNonJwtPrincipal_shouldThrow() {
        var auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("not-a-jwt");

        SecurityContextHolder.getContext().setAuthentication(auth);

        var result = service.getCurrentUserId();
        assertTrue(result.isEmpty());
    }

    @Test
    void getCurrentUserRoles_withValidAuth_shouldReturnRoles() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_MANAGER"));

        var auth = mock(Authentication.class);
        doReturn(authorities).when(auth).getAuthorities();

        SecurityContextHolder.getContext().setAuthentication(auth);

        var result = service.getCurrentUserRoles();
        assertEquals(2, result.size());
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("MANAGER"));
    }

    @Test
    void getCurrentUserRoles_withNoAuth_shouldReturnEmpty() {
        SecurityContextHolder.clearContext();
        var result = service.getCurrentUserRoles();
        assertTrue(result.isEmpty());
    }
}
