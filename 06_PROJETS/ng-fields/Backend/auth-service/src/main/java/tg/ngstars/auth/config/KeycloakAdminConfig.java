package tg.ngstars.auth.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Bean
    public Keycloak keycloak(KeycloakProperties props) {
        // ponytail: timeouts configured via KeycloakProperties/HTTP client config
        return KeycloakBuilder.builder()
                .serverUrl(props.authServerUrl())
                .realm(props.realm())
                .clientId(props.adminClientId())
                .clientSecret(props.adminClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
