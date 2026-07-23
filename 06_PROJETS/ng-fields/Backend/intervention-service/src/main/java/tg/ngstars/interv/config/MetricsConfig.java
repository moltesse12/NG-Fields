package tg.ngstars.interv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter interventionsCreatedCounter(MeterRegistry registry) {
        return Counter.builder("interventions.created")
                .description("Nombre total d'interventions creees")
                .tag("type", "counter")
                .register(registry);
    }

    @Bean
    public Counter interventionsCompletedCounter(MeterRegistry registry) {
        return Counter.builder("interventions.completed")
                .description("Nombre total d'interventions cloturees")
                .tag("type", "counter")
                .register(registry);
    }

    @Bean
    public Counter syncBatchCounter(MeterRegistry registry) {
        return Counter.builder("sync.batch.count")
                .description("Nombre de batches sync recus")
                .tag("type", "counter")
                .register(registry);
    }

    @Bean
    public Timer syncBatchTimer(MeterRegistry registry) {
        return Timer.builder("sync.batch.duration")
                .description("Duree des batches sync")
                .tag("type", "timer")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Bean
    public Counter emailsSentCounter(MeterRegistry registry) {
        return Counter.builder("emails.sent")
                .description("Nombre d'emails envoyes")
                .tag("type", "counter")
                .register(registry);
    }
}
