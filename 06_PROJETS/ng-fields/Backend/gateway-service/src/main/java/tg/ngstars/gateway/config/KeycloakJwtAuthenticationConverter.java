package tg.ngstars.gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reactive JWT converter for Keycloak tokens in the WebFlux gateway.
 * <p>
 * Extracts realm_access roles from Keycloak JWT and maps them to Spring Security authorities.
 * Non-reactive equivalent: {@code tg.ngstars.common.security.RealmRoleConverter} (servlet-based).
 */
public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        var authorities = extractRealmRoles(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities, jwt.getSubject()));
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Object raw = jwt.getClaim("realm_access");
        if (!(raw instanceof Map<?, ?> realmAccess)) return List.of();
        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof List<?> roles)) return List.of();
        return roles.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .collect(Collectors.toUnmodifiableList());
    }
}
