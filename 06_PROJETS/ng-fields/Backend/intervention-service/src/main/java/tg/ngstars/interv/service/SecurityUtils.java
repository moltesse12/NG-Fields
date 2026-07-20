package tg.ngstars.interv.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Jwt jwt)
                || jwt.getSubject() == null)
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException("No authenticated user");
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException("Invalid user ID in token");
        }
    }

    public Set<String> getCurrentUserRoles() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return Set.of();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.toSet());
    }

    public boolean isAdminOrManager() {
        var roles = getCurrentUserRoles();
        return roles.contains("ADMIN") || roles.contains("MANAGER");
    }
}
