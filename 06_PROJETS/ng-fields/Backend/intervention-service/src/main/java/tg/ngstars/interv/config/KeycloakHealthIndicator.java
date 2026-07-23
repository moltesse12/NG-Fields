package tg.ngstars.interv.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class KeycloakHealthIndicator implements HealthIndicator {

    private final org.springframework.core.env.Environment env;

    public KeycloakHealthIndicator(org.springframework.core.env.Environment env) {
        this.env = env;
    }

    @Override
    public Health health() {
        var issuerUri = env.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri", "unknown");
        try {
            var url = issuerUri.replace("/realms/ng-fields", "");
            var conn = java.net.URI.create(url).toURL().openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.connect();
            return Health.up()
                    .withDetail("keycloak", "reachable")
                    .withDetail("issuer", issuerUri)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("keycloak", "unreachable")
                    .withDetail("issuer", issuerUri)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
