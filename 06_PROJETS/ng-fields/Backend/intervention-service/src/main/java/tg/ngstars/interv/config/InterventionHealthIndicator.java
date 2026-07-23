package tg.ngstars.interv.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import tg.ngstars.interv.repository.InterventionRepository;

@Component
public class InterventionHealthIndicator implements HealthIndicator {

    private final InterventionRepository interventionRepository;

    public InterventionHealthIndicator(InterventionRepository interventionRepository) {
        this.interventionRepository = interventionRepository;
    }

    @Override
    public Health health() {
        try {
            long count = interventionRepository.count();
            return Health.up()
                    .withDetail("interventionCount", count)
                    .withDetail("database", "reachable")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "unreachable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
