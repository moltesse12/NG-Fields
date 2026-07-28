package tg.ngstars.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
    String authServerUrl,
    String adminClientId,
    String adminClientSecret,
    String realm,
    String verificationClientId,
    String verificationClientSecret
) {}
