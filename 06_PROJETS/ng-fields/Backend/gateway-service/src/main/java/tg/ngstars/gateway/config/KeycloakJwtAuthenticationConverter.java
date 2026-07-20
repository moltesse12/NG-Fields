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

public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        var authorities = extractRoles(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities, jwt.getSubject()));
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        Object raw = jwt.getClaim("realm_access");
        if (!(raw instanceof Map<?, ?> realmAccess)) return List.of();
        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof List<?> roles)) return List.of();
        return roles.stream()
            .filter(r -> r instanceof String)
            .map(r -> new SimpleGrantedAuthority("ROLE_" + ((String) r).toUpperCase()))
            .collect(Collectors.toUnmodifiableList());
    }
}
