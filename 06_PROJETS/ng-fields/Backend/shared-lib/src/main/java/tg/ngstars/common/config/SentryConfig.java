package tg.ngstars.common.config;

import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfig {

    private static final Logger log = LoggerFactory.getLogger(SentryConfig.class);

    @Value("${sentry.dsn:}")
    private String dsn;

    @PostConstruct
    public void init() {
        if (dsn != null && !dsn.isBlank()) {
            Sentry.init(options -> {
                options.setDsn(dsn);
                options.setTracesSampleRate(0.2);
            });
            log.info("Sentry initialized with DSN: {}...", dsn.substring(0, Math.min(20, dsn.length())));
        } else {
            log.info("Sentry DSN not configured, error tracking disabled");
        }
    }
}
